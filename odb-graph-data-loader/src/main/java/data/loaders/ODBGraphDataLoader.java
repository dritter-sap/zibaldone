package data.loaders;

import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.*;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.record.OVertex;
import data.MemoryUtils;
import me.tongfei.progressbar.ProgressBar;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ODBGraphDataLoader implements GraphDataLoader {
  private static final Logger log = LoggerFactory.getLogger(ODBGraphDataLoader.class);

  private OrientDB      orient;
  private ODatabasePool pool; //pool not thread-safe

  @Override
  public void connect(final String serverName, final Integer serverPort, final String dbName, final String userName,
                      final String password) throws Exception {
    log.debug("Connecting to " + serverName + ":" + serverPort + "(db=" + dbName + ")");
    final OrientDBConfigBuilder poolCfg = OrientDBConfig.builder();
    poolCfg.addConfig(OGlobalConfiguration.DB_POOL_MIN, 5);
    poolCfg.addConfig(OGlobalConfiguration.DB_POOL_MAX, 10);
    final OrientDBConfig oriendDBconfig = poolCfg.build();
    orient = new OrientDB("remote:" + serverName, userName, password, oriendDBconfig);
    orient.create(dbName, ODatabaseType.PLOCAL);
    pool = new ODatabasePool(orient, dbName, "admin", "admin", oriendDBconfig);
  }

  @Override
  public void disconnect(final String dbName) {
    log.debug("Disconnecting from " + dbName);
    pool.close();
    orient.drop(dbName);
    orient.close();
  }

  @Override
  public Map<String, OVertex> loadVertexKeys(final CSVParser records, final String odbVertexClassName, final String[] vertexHeader,
                                             final String vertexKeyFieldName, final BatchCoordinator bc, final long expectedMax) {
    try (ProgressBar pb = new ProgressBar("Vertices", expectedMax)) {
      final Map<String, OVertex> vertices = new HashMap<>();
      try (final ODatabaseSession session = pool.acquire()) {
        session.createVertexClass(odbVertexClassName);

        bc.begin(session);
        for (final CSVRecord record : records) {
          final OVertex vertex = createVertex(odbVertexClassName, vertexHeader, session, record, vertexKeyFieldName);
          vertices.put(record.get(vertexKeyFieldName), vertex);
          bc.iterate(session, records.getRecordNumber(), pb);
        }
        bc.end(session);
        pb.stepTo(expectedMax);
      }
      log.debug("Mem(used/max) {}/{}", MemoryUtils.usedMemoryInMB(), MemoryUtils.maxMemoryInMB());
      return vertices;
    }
  }

  @Override
  public void loadEdges(final CSVParser records, final String odbEdgeClassName, final String[] edgeHeader,
                        final String edgeSrcKeyFieldName, final String edgeTrgtKeyFieldName, Map<String, OVertex> vertices,
                        final BatchCoordinator bc, long expectedMax) {
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
        log.debug("Mem(used/max) {}/{}", MemoryUtils.usedMemoryInMB(), MemoryUtils.maxMemoryInMB());
      }
    }
  }

  @Override
  public void loadVertexProperties(final CSVParser records, final String[] vertexHeader, final String vertexKeyFieldName,
                                   final BatchCoordinator bc, final Map<String, OVertex> vertices) {
    try (ProgressBar pb = new ProgressBar("Vertices", vertices.size())) {
      try (final ODatabaseSession session = pool.acquire()) {
        bc.begin(session);
        for (final CSVRecord record : records) {
          final OVertex vertex = vertices.get(record.get(vertexKeyFieldName));
          this.addVertexProperties(vertex, vertexHeader, record, vertexKeyFieldName);
          vertices.remove(vertex.getProperty(vertexKeyFieldName));
          bc.iterate(session, records.getRecordNumber(), pb);
        }
        bc.end(session);
        pb.stepTo(vertices.size());
      }
      log.debug("Mem(used/max) {}/{}", MemoryUtils.usedMemoryInMB(), MemoryUtils.maxMemoryInMB());
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

  private void createEdge(String odbEdgeClassName, String[] edgeHeader, String edgeSrcKeyFieldName, String edgeTrgtKeyFieldName, Map<String, OVertex> vertices, ODatabaseSession session, CSVRecord record) {
    final String src = record.get(edgeSrcKeyFieldName);
    final String trgt = record.get(edgeTrgtKeyFieldName);
    final OEdge edge = session.newEdge(vertices.get(src), vertices.get(trgt), odbEdgeClassName);
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
