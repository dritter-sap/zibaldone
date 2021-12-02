import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.*;

public class OdbConnector {
    private OrientDB orient;
    private ODatabasePool pool;

    public void connect(final String serverName, final int serverPort, final String dbName, final String userName,
                        final String password) throws Exception {
        final OrientDBConfigBuilder poolCfg = OrientDBConfig.builder();
        poolCfg.addConfig(OGlobalConfiguration.DB_POOL_MIN, 5);
        poolCfg.addConfig(OGlobalConfiguration.DB_POOL_MAX, 10);
        final OrientDBConfig oriendDBconfig = poolCfg.build();
        orient = new OrientDB(serverName, userName, password, oriendDBconfig);
        // orient.createIfNotExists(dbName, ODatabaseType.PLOCAL);
        // if (!orient.isOpen()) {
        //     orient.open(dbName, userName, password);
        // }
        pool = new ODatabasePool(orient, dbName, userName, password);
    }

    public void initAndGetDatabaseUrlAndDbType(String url, final String serverUser, final String serverPassword) {
        if (url.startsWith("remote:")) {
            final OrientDBConfigBuilder poolCfg = OrientDBConfig.builder();
            poolCfg.addConfig(OGlobalConfiguration.DB_POOL_MIN, 5);
            poolCfg.addConfig(OGlobalConfiguration.DB_POOL_MAX, 10);
            final OrientDBConfig oriendDBconfig = poolCfg.build();
            if (orient == null) {
                orient = new OrientDB(url, serverUser, serverPassword, oriendDBconfig);
            }
        } else if (url.startsWith("memory:")) {
            url = "embedded:";
            if (orient == null) {
                orient = new OrientDB(url, OrientDBConfig.defaultConfig());
            }
        } else {
            if (orient == null) {
                orient = new OrientDB(url, OrientDBConfig.defaultConfig());
            }
        }
    }

    public void initPool(final String databaseName, final String userName, final String password) {
        if (pool == null) {
            pool = new ODatabasePool(orient, databaseName, userName, password);
        }
    }

    public void disconnect() {
        orient.close();
    }

    public ODatabasePool getPool() {
        return pool;
    }
}
