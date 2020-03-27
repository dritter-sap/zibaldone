package data.loaders;

import com.orientechnologies.orient.core.record.OVertex;
import org.apache.commons.csv.CSVParser;

import java.util.Map;

public interface GraphDataLoader {
  void connect(String serverName, Integer serverPort, String dbName, String userName,
               String password) throws Exception;

  void disconnect(String dbName);

  Map<String, OVertex> loadVertices(CSVParser records, String odbVertexClassName, String[] vertexHeader,
                                    String vertexKeyFieldName, final BatchCoordinator bc);

  void loadEdges(CSVParser records, String odbEdgeClassName, String[] edgeHeader, String edgeSrcKeyFieldName,
                 String edgeTrgtKeyFieldName, Map<String, OVertex> vertices, final BatchCoordinator bc);
}
