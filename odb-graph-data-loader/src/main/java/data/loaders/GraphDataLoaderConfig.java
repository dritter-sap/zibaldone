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

    public GraphDataLoaderConfig(String dbName, String serverName, Integer serverPort, long batchSize) {
        this.dbName = dbName;
        this.serverName = serverName;
        this.serverPort = serverPort;
        this.batchSize = batchSize;
    }

    public static GraphDataLoaderConfig load(final Properties props) {
        final GraphDataLoaderConfig config = new GraphDataLoaderBuilder()
            .dbName(props.getProperty("DB_NAME"))
            .serverName(props.getProperty("SERVER_NAME"))
            .serverPort(Integer.valueOf(props.getProperty("SERVER_PORT")))
            .batchSize(Long.valueOf(props.getProperty("BATCH_SIZE")))
            .build();
        return config;
    }

    public void logSelectedApplicationParameters() {
        log.debug("Application Parameters");
        log.debug("--------------------------------------------------");
        log.debug("\tserver:port   = {}:{}", this.getServerName(), this.getServerPort());
        log.debug("\tdatabase name = {}", this.getDbName());
        log.debug("\tbatch size    = {}", this.batchSize);
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
}
