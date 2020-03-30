package data.loaders;

import com.orientechnologies.orient.core.record.OVertex;
import org.apache.commons.csv.CSVParser;

import java.util.Map;

public interface GraphDataLoader {
  void connect(String serverName, Integer serverPort, String dbName, String userName,
               String password) throws Exception;

  void disconnect(String dbName);

  Map<String, OVertex> loadVertexKeys(CSVParser records, String odbVertexClassName, String[] vertexHeader,
                                      String vertexKeyFieldName, BatchCoordinator bc, long numberVertices);

  void loadEdges(CSVParser records, String odbEdgeClassName, String[] edgeHeader, String edgeSrcKeyFieldName,
                 String edgeTrgtKeyFieldName, Map<String, OVertex> vertices, BatchCoordinator bc, long expectedMax);

  void loadVertexProperties(CSVParser records, String[] vertexHeader, String vertexKeyFieldName, BatchCoordinator bc,
                            Map<String, OVertex> vertices);
}
