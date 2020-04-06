package data.loaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class GraphDataLoaderConfig {
    private static final Logger log = LoggerFactory.getLogger(GraphDataLoaderConfig.class);

    private final String  dbName;
    private final String  serverName;
    private final Integer serverPort;
    private final long    batchSize;
    private final long    numberVertices;
    private final long    numberEdges;
    private final boolean cleanup;

    public GraphDataLoaderConfig(String dbName, String serverName, Integer serverPort, long batchSize, long numberVertices,
                                 long numberEdges, boolean cleanup) {
        this.dbName = dbName;
        this.serverName = serverName;
        this.serverPort = serverPort;
        this.batchSize = batchSize;
        this.numberVertices = numberVertices;
        this.numberEdges = numberEdges;
        this.cleanup = cleanup;
    }

    public static GraphDataLoaderConfig load(final Properties props) {
        final GraphDataLoaderConfig config = new GraphDataLoaderBuilder()
            .dbName(props.getProperty("DB_NAME"))
            .serverName(props.getProperty("SERVER_NAME"))
            .serverPort(Integer.valueOf(props.getProperty("SERVER_PORT")))
            .batchSize(Long.valueOf(props.getProperty("BATCH_SIZE")))
            .numberVertices(Long.valueOf(props.getProperty("NUMBER_VERTICES")))
            .numberEdges(Long.valueOf(props.getProperty("NUMBER_EDGES")))
            .cleanup(Boolean.valueOf(props.getProperty("CLEANUP")))
            .build();
        return config;
    }

    public void logSelectedApplicationParameters() {
        log.debug("Application Parameters");
        log.debug("--------------------------------------------------");
        log.debug("\tserver:port   = {}:{}", this.getServerName(), this.getServerPort());
        log.debug("\tdatabase name = {}", this.getDbName());
        log.debug("\tbatch size    = {}", this.batchSize);
        log.debug("\t#vertices     = {}", this.numberVertices);
        log.debug("\t#edges        = {}", this.numberEdges);
        log.debug("Memory total {}, free {}", Runtime.getRuntime().totalMemory(), Runtime.getRuntime().freeMemory());
        log.debug("\t#cleanup      = {}", this.cleanup);
    }

    public String getDbName() {
        return dbName;
    }

    public String getServerName() {
        return serverName;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public long getBatchSize() {
        return batchSize;
    }

    public long getNumberVertices() {
        return this.numberVertices;
    }

    public long getNumberEdges() {
        return this.numberEdges;
    }

    public boolean getCleanup() {
        return this.cleanup;
    }
}
