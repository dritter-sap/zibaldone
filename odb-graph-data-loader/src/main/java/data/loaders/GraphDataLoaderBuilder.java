package data.loaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphDataLoaderBuilder {
    private static final Logger log = LoggerFactory.getLogger(GraphDataLoaderBuilder.class);

    private String  dbName;
    private String  serverName;
    private Integer serverPort;
    private long batchSize;
    private Long numberVertices;
    private Long numberEdges;

    public GraphDataLoaderConfig build() {
        return new GraphDataLoaderConfig(dbName, serverName, serverPort, batchSize, numberVertices, numberEdges);
    }

    public GraphDataLoaderBuilder dbName(final String dbName) {
        this.dbName = dbName;
        return this;
    }

    public GraphDataLoaderBuilder serverName(final String serverName) {
        this.serverName = serverName;
        return this;
    }

    public GraphDataLoaderBuilder serverPort(final Integer serverPort) {
        this.serverPort = serverPort;
        return this;
    }

    public GraphDataLoaderBuilder batchSize(final long batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public GraphDataLoaderBuilder numberVertices(final Long numberVertices) {
        this.numberVertices = numberVertices;
        return this;
    }

    public GraphDataLoaderBuilder numberEdges(Long numberEdges) {
        this.numberEdges = numberEdges;
        return this;
    }
}