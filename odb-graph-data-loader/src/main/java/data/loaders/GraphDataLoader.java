package data.loaders;

import com.orientechnologies.orient.core.id.ORID;
import data.utils.TransientKeyPersistentValueMap;
import org.apache.commons.csv.CSVParser;

import java.util.Map;

public interface GraphDataLoader {
  void connect(String serverName, Integer serverPort, String dbName, String userName, String password, boolean createDB);

  void disconnect(String dbName, boolean cleanup);

  Map<String, ORID> loadVertexKeys(CSVParser records, String odbVertexClassName, String[] vertexHeader,
                                   String vertexKeyFieldName, BatchCoordinator bc, long numberVertices);

  void loadEdges(CSVParser records, String odbEdgeClassName, String[] edgeHeader, String edgeSrcKeyFieldName,
                 String edgeTrgtKeyFieldName, Map<String, ORID> vertices, BatchCoordinator bc, long expectedMax);

  void loadVertexProperties(CSVParser records, String[] vertexHeader, String vertexKeyFieldName, BatchCoordinator bc,
                            Map<String, ORID> vertices);

  void verify(BatchCoordinator bc, String vertexClass, String edgeClass, long expectedNumberVertices,
              long expectedNumberEdges);

  void query(BatchCoordinator bc, String...query);

  void withPersistentMap(boolean persistentmap);
}
