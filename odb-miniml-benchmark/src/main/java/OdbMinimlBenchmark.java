import com.orientechnologies.orient.core.db.*;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.HashMap;
import java.util.Map;

public class OdbMinimlBenchmark {
    private static final int ITERATIONS = 100;
    public static final String QUERY_2 = "SELECT characteristics FROM (SELECT expand(channels) FROM (SELECT expand(outE(\"has_sample\").inV()) FROM series WHERE iid = :key))";
    public static final String QUERY_6 = "SELECT * FROM series WHERE platforms containsAny (select * from platform where iid = :key)";
    public static final String QUERY_5 = "SELECT * FROM sample WHERE channels containsAny (select * from channel where characteristics.treatment_raw = :parameter)";
    public static final String QUERY_4 = "SELECT * FROM sample where channels containsAny (select * from channel where characteristics containsKey :parameter)";
    public static final String QUERY_3 = "SELECT characteristics FROM (SELECT expand(channels) FROM sample WHERE iid = :key)";
    public static final String QUERY_1 = "SELECT * FROM series WHERE iid = :key";

    private final OdbConnector odbc = new OdbConnector();

    public static void main(String[] args) {
        final OdbMinimlBenchmark odbBench = new OdbMinimlBenchmark();
        try {
            odbBench.execute("remote:" + args[0], "zetomap", args[1], args[2]);
            odbBench.execute("remote:" + args[0], "zetomap_idx", args[1], args[2]);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void execute(final String url, final String databaseName, final String userName, final String password) throws Exception {
        System.out.println("Checking database: " + databaseName);
        try {
            odbc.connect(url, 2424, databaseName, userName, password);
            this.executeQuery1();
            this.executeQuery2();
            this.executeQuery3();
            this.executeQuery4();
            this.executeQuery5();
            this.executeQuery6();
        } finally {
            odbc.disconnect();
        }
    }

    private void executeQuery1() {
        final SummaryStatistics stats = new SummaryStatistics();

        Map<String, Object> params = new HashMap<>();
        params.put("key", "GSE105766");
        stats.addValue(executeAndMeasureQuery(QUERY_1, params));

        params = new HashMap<>();
        params.put("key", "GSE164728");
        stats.addValue(executeAndMeasureQuery(QUERY_1, params));

        params = new HashMap<>();
        params.put("key", "GSE105766");
        stats.addValue(executeAndMeasureQuery(QUERY_1, params));

        System.out.println("Query1," + stats.getMean() / 1000000 + " ms");
    }

    private void executeQuery2() {
        final SummaryStatistics stats = new SummaryStatistics();
        Map<String, Object> params = new HashMap<>();
        params.put("key", "GSE163826");
        stats.addValue(executeAndMeasureQuery(QUERY_2, params));

        params = new HashMap<>();
        params.put("key", "GSE152189");
        stats.addValue(executeAndMeasureQuery(QUERY_2, params));

        params = new HashMap<>();
        params.put("key", "GSE150046");
        stats.addValue(executeAndMeasureQuery(QUERY_2, params));

        System.out.println("Query2," + stats.getMean() / 1000000 + " ms");
    }

    private void executeQuery3() {
        final SummaryStatistics stats = new SummaryStatistics();
        Map<String, Object> params = new HashMap<>();
        params.put("key", "GSM4521284");
        stats.addValue(executeAndMeasureQuery(QUERY_3, params));

        params = new HashMap<>();
        params.put("key", "GSM5028203");
        stats.addValue(executeAndMeasureQuery(QUERY_3, params));

        params = new HashMap<>();
        params.put("key", "GSM4568913");
        stats.addValue(executeAndMeasureQuery(QUERY_3, params));

        System.out.println("Query3," + stats.getMean() / 1000000 + " ms");
    }

    private void executeQuery4() {
        final SummaryStatistics stats = new SummaryStatistics();
        Map<String, Object> params = new HashMap<>();
        params.put("parameter", "treatment_raw");

        stats.addValue(executeAndMeasureQuery(QUERY_4, params));
        stats.addValue(executeAndMeasureQuery(QUERY_4, params));
        stats.addValue(executeAndMeasureQuery(QUERY_4, params));

        System.out.println("Query4," + stats.getMean() / 1000000 + " ms");
    }

    private void executeQuery5() {
        final SummaryStatistics stats = new SummaryStatistics();
        Map<String, Object> params = new HashMap<>();
        params.put("parameter", "H1");
        stats.addValue(executeAndMeasureQuery(QUERY_5, params));

        params = new HashMap<>();
        params.put("parameter", "wild type");
        stats.addValue(executeAndMeasureQuery(QUERY_5, params));

        params = new HashMap<>();
        params.put("parameter", "zebrafish neuromast hair cells");
        stats.addValue(executeAndMeasureQuery(QUERY_5, params));

        System.out.println("Query5," + stats.getMean() / 1000000 + " ms");
    }

    private void executeQuery6() {
        final SummaryStatistics stats = new SummaryStatistics();
        Map<String, Object> params = new HashMap<>();
        params.put("key", "GPL24995");
        stats.addValue(executeAndMeasureQuery(QUERY_6, params));

        params = new HashMap<>();
        params.put("key", "GPL14875");
        stats.addValue(executeAndMeasureQuery(QUERY_6, params));

        params = new HashMap<>();
        params.put("key", "GPL25922");
        stats.addValue(executeAndMeasureQuery(QUERY_6, params));

        System.out.println("Query6," + stats.getMean() / 1000000 + " ms");
    }


    private double executeAndMeasureQuery(final String query, final Map<String, Object> params) {
        final SummaryStatistics stats = new SummaryStatistics();
        for (int i = 0; i < ITERATIONS; i++) {
            long start = System.nanoTime();
            try (final ODatabaseSession session = odbc.getPool().acquire(); final OResultSet rs = session.query(query, params)) {
            }
            long runningTime = System.nanoTime() - start;
            stats.addValue(runningTime);
        }
        return stats.getMean();
    }
}
