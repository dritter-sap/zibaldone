package data.loaders;

import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.*;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import data.MemoryUtils;
import data.utils.TransientKeyPersistentValueMap;
import data.utils.ObjectExistsException;
import data.utils.VersionMismatchException;
import me.tongfei.progressbar.ProgressBar;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ODBGraphDataLoader implements GraphDataLoader {
  private static final Logger log = LoggerFactory.getLogger(ODBGraphDataLoader.class);

  private static final String persistentMapPathPrefix = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator")
      + "odb" + System.getProperty("file.separator");

  private OrientDB      orient;
  private ODatabasePool pool; // pool not thread-safe

  @Override
  public void connect(final String serverName, final Integer serverPort, final String dbName, final String userName,
                      final String password, final boolean createDB) {
    log.debug("Connecting to " + serverName + ":" + serverPort + "(db=" + dbName + ")");
    final OrientDBConfigBuilder poolCfg = OrientDBConfig.builder();
    poolCfg.addConfig(OGlobalConfiguration.DB_POOL_MIN, 5);
    poolCfg.addConfig(OGlobalConfiguration.DB_POOL_MAX, 10);
    final OrientDBConfig oriendDBconfig = poolCfg.build();
    if (serverName.startsWith("remote:")) {
      // remote:<host> can be called like that
      orient = new OrientDB(serverName, userName, password, oriendDBconfig);
    } else if (serverName.startsWith("embedded:")) {
      // embedded:/<path>/directory + server can be called like that
      orient = new OrientDB(serverName, OrientDBConfig.defaultConfig());
    } else {
      throw new UnsupportedOperationException("Currently only 'embedded' and 'remote' are supported.");
    }
    if (createDB) {
      orient.create(dbName, ODatabaseType.PLOCAL);
    }
    pool = new ODatabasePool(orient, dbName, "admin", "admin", oriendDBconfig);
  }

  @Override
  public void disconnect(final String dbName, boolean cleanup) {
    log.debug("Disconnecting from " + dbName + " cleanup:" + cleanup);
    pool.close();
    if (cleanup) {
      orient.drop(dbName);
    }
    orient.close();

    try {
      FileUtils.deleteDirectory(new File(persistentMapPathPrefix));
    } catch (final IOException e) {
      log.error("Persistent map files were not removed from {}", persistentMapPathPrefix);
    }
  }

  @Override
  public Map<String, ORID> loadVertexKeys(final CSVParser records, final String odbVertexClassName,
                                             final String[] vertexHeader, final String vertexKeyFieldName,
                                             final BatchCoordinator bc, final long expectedMax) {
    try (final ProgressBar pb = new ProgressBar("Vertex keys", expectedMax)) {
      // final Map<String, ORID> vertices = new HashMap<>();
      createDirIfNotExists(persistentMapPathPrefix);
      final TransientKeyPersistentValueMap<String, ORID> vertices = new TransientKeyPersistentValueMap<>(
          persistentMapPathPrefix + "odb", 0);
      try (final ODatabaseSession session = pool.acquire()) {
        session.createVertexClass(odbVertexClassName);

        final Map<String, OElement> batchLocalVertices = new HashMap<>();
        bc.begin(session);
        for (final CSVRecord record : records) {
          final OVertex vertex = createVertex(odbVertexClassName, vertexHeader, session, record, vertexKeyFieldName);
          // vertices.put(record.get(vertexKeyFieldName), vertex);
          batchLocalVertices.put(record.get(vertexKeyFieldName), vertex);
          bc.iterateVertices(session, records.getRecordNumber(), pb, batchLocalVertices, vertices);
        }
        bc.end(session);
        for (final Map.Entry<String, OElement> tmp : batchLocalVertices.entrySet()) {
          vertices.put(tmp.getKey(), tmp.getValue().getIdentity());
        }
        pb.setExtraMessage("Clearing batch local");
        batchLocalVertices.clear(); // reset batch local instances
        pb.stepTo(expectedMax);
      }
      log.debug("Mem(used/max) {}/{}", MemoryUtils.usedMemoryInMB(), MemoryUtils.maxMemoryInMB());
      pb.setExtraMessage("Saving file map values");
      vertices.save();
      MemoryUtils.freeMemory();
      return vertices;
    } catch (final IOException | ObjectExistsException | VersionMismatchException | ClassNotFoundException e) {
      throw new IllegalStateException("Error in persistent map", e);
    }
  }

  private void createDirIfNotExists(String pathPrefix) {
    log.debug("Persistent map directory: {}", pathPrefix);
    final File directory = new File(String.valueOf(pathPrefix));
    if (!directory.exists()) {
      directory.mkdir();
    }
  }

  @Override
  public void loadEdges(final CSVParser records, final String odbEdgeClassName, final String[] edgeHeader,
                        final String edgeSrcKeyFieldName, final String edgeTrgtKeyFieldName,
                        final Map<String, ORID> vertices, final BatchCoordinator bc, long expectedMax) {
    try (ProgressBar pb = new ProgressBar("Edges", expectedMax)) {
      try (final ODatabaseSession session = pool.acquire()) {
        session.createEdgeClass(odbEdgeClassName);

        bc.begin(session);
        for (final CSVRecord record : records) {
          this.createEdge(odbEdgeClassName, edgeHeader, edgeSrcKeyFieldName, edgeTrgtKeyFieldName, vertices, session, record);
          bc.iterate(session, records.getRecordNumber(), pb);
        }
        bc.end(session);
        pb.stepTo(expectedMax);
      }
      MemoryUtils.freeMemory();
      log.debug("Mem(used/max) {}/{}", MemoryUtils.usedMemoryInMB(), MemoryUtils.maxMemoryInMB());
    }
  }

  @Override
  public void loadVertexProperties(final CSVParser records, final String[] vertexHeader, final String vertexKeyFieldName,
                                   final BatchCoordinator bc, final TransientKeyPersistentValueMap<String, ORID> vertices) {
    try (ProgressBar pb = new ProgressBar("Vertex props", vertices.size())) {
      try (final ODatabaseSession session = pool.acquire()) {
        bc.begin(session);
        for (final CSVRecord record : records) {
          session.load(vertices.get(record.get(vertexKeyFieldName)));
          final OVertex vertex = session.load(vertices.get(record.get(vertexKeyFieldName)));
          this.addVertexProperties(vertex, vertexHeader, record, vertexKeyFieldName);
          // vertices.remove(vertex.getProperty(vertexKeyFieldName));
          bc.iterate(session, records.getRecordNumber(), pb);
        }
        bc.end(session);
        pb.stepTo(vertices.size());
        vertices.removeAll();
      }
      MemoryUtils.freeMemory();
      log.debug("Mem(used/max) {}/{}", MemoryUtils.usedMemoryInMB(), MemoryUtils.maxMemoryInMB());
    }
  }

  @Override
  public void verify(final BatchCoordinator bc, final String vertexClass, final String edgeClass,
                     final long expectedNumberVertices, final long expectedNumberEdges) {
    try (final ProgressBar pb = new ProgressBar("Verification", 2)) {
      try (final ODatabaseSession session = pool.acquire()) {
        bc.begin(session);

        OResultSet resultSet = session.query("select count(*) from `" + vertexClass + "`");
        checkNumberElements(expectedNumberVertices, resultSet, "count(*)");
        pb.step();
        resultSet = session.query("select count(*) from `" + edgeClass + "`");
        checkNumberElements(expectedNumberEdges, resultSet, "count(*)");
        pb.step();

        bc.end(session);
      }
      log.debug("Mem(used/max) {}/{}", MemoryUtils.usedMemoryInMB(), MemoryUtils.maxMemoryInMB());
    }
  }

  @Override
  public void query(final BatchCoordinator bc, final String...queries) {
    try (final ProgressBar pb = new ProgressBar("Queries", queries.length)) {
      try (final ODatabaseSession session = pool.acquire()) {
        bc.begin(session);

        for (final String query : queries) {
          final OResultSet resultSet = session.query(query);
          pb.step();
        }
        bc.end(session);
      }
      log.debug("Mem(used/max) {}/{}", MemoryUtils.usedMemoryInMB(), MemoryUtils.maxMemoryInMB());
    }
  }

  private void checkNumberElements(final long expectedNumberElements, final OResultSet resultSet, final String propertyName) {
    if (resultSet.hasNext()) {
      final OResult result = resultSet.next();
      final long actualNumberElements = result.getProperty(propertyName);
      if (actualNumberElements != expectedNumberElements) {
        log.error("Vertices missing(expected {}, actual {})", expectedNumberElements, actualNumberElements);
        return;
      }
      log.debug("All Elements loaded {} for {}", expectedNumberElements, propertyName);
    }
  }

  private OVertex createVertex(String odbVertexClassName, String[] vertexHeader, ODatabaseSession session,
                               CSVRecord record, String vertexKeyFieldName) {
    final OVertex vertex = session.newVertex(odbVertexClassName);
    for (int i=0; i<vertexHeader.length; i++) {
      if (vertexHeader[i].equals(vertexKeyFieldName)) {
        setPropertyIfNotNullOrEmpty(vertex, vertexHeader[i], record.get(i));
      }
    }
    vertex.save();
    return vertex;
  }

  private void addVertexProperties(final OVertex vertex, final String[] vertexHeaders, final CSVRecord record,
                                   final String vertexKeyFieldName) {
    for (int i=0; i<vertexHeaders.length; i++) {
      if (!vertexHeaders[i].equals(vertexKeyFieldName)) {
        setPropertyIfNotNullOrEmpty(vertex, vertexHeaders[i], record.get(i));
      }
    }
    vertex.save();
  }

  private void createEdge(final String odbEdgeClassName, final String[] edgeHeader, final String edgeSrcKeyFieldName,
                          final String edgeTrgtKeyFieldName, final Map<String, ORID> vertices,
                          final ODatabaseSession session, final CSVRecord record) {
    final String src = record.get(edgeSrcKeyFieldName);
    final String trg = record.get(edgeTrgtKeyFieldName);

    final OEdge edge = session.newEdge(session.load(vertices.get(src)), session.load(vertices.get(trg)), odbEdgeClassName);
    for (int i = 0; i < edgeHeader.length; i++) {
      setPropertyIfNotNullOrEmpty(edge, edgeHeader[i], record.get(i));
    }
    edge.save();
  }

  private void setPropertyIfNotNullOrEmpty(final OElement element, final String header, final String value) {
    if (null != value && !value.isEmpty()) {
      element.setProperty(header, value);
    }
  }
}
