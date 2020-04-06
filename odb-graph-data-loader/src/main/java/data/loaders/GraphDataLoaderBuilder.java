package data.loaders;

public class GraphDataLoaderBuilder {
    private String  dbName;
    private String  serverName;
    private Integer serverPort;
    private long batchSize;
    private Long numberVertices;
    private Long numberEdges;
    private boolean cleanup;

    public GraphDataLoaderConfig build() {
        return new GraphDataLoaderConfig(dbName, serverName, serverPort, batchSize, numberVertices, numberEdges, cleanup);
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

    public GraphDataLoaderBuilder numberVertices(final long numberVertices) {
        this.numberVertices = numberVertices;
        return this;
    }

    public GraphDataLoaderBuilder numberEdges(final long numberEdges) {
        this.numberEdges = numberEdges;
        return this;
    }

    public GraphDataLoaderBuilder cleanup(final boolean cleanup) {
        this.cleanup = cleanup;
        return this;
    }
}