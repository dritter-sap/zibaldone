import com.orientechnologies.orient.core.db.*;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.HashMap;
import java.util.Map;

public class OdbMinimlBenchmark {
    private static final int ITERATIONS = 100;

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
        // System.out.println("Checkingquery1...");

        final SummaryStatistics stats = new SummaryStatistics();
        Map<String, Object> params = new HashMap<>();
        params.put("key", "GSE105766");
        stats.addValue(executeQuery("SELECT * FROM series WHERE iid = :key", params));

        params = new HashMap<>();
        params.put("key", "GSE164728");
        stats.addValue(executeQuery("SELECT * FROM series WHERE iid = :key", params));

        params = new HashMap<>();
        params.put("key", "GSE105766");
        stats.addValue(executeQuery("SELECT * FROM series WHERE iid = :key", params));

        System.out.println("Query1," + stats.getMean() / 1000000 + " ms");
    }

    private void executeQuery2() {
        // System.out.println("Checkingquery2...");

        final SummaryStatistics stats = new SummaryStatistics();
        Map<String, Object> params = new HashMap<>();
        params.put("key", "GSE163826");
        stats.addValue(executeQuery("SELECT characteristics FROM (SELECT expand(channels) FROM (SELECT expand(outE(\"has_sample\").inV()) FROM series WHERE iid = :key))", params));

        params = new HashMap<>();
        params.put("key", "GSE152189");
        stats.addValue(executeQuery("SELECT characteristics FROM (SELECT expand(channels) FROM (SELECT expand(outE(\"has_sample\").inV()) FROM series WHERE iid = :key))", params));

        params = new HashMap<>();
        params.put("key", "GSE150046");
        stats.addValue(executeQuery("SELECT characteristics FROM (SELECT expand(channels) FROM (SELECT expand(outE(\"has_sample\").inV()) FROM series WHERE iid = :key))", params));

        System.out.println("Query2," + stats.getMean() / 1000000 + " ms");
    }

    private void executeQuery3() {
        // System.out.println("Checkingquery3...");

        final SummaryStatistics stats = new SummaryStatistics();
        Map<String, Object> params = new HashMap<>();
        params.put("key", "GSM4521284");
        stats.addValue(executeQuery("SELECT characteristics FROM (SELECT expand(channels) FROM sample WHERE iid = :key)", params));

        params = new HashMap<>();
        params.put("key", "GSM5028203");
        stats.addValue(executeQuery("SELECT characteristics FROM (SELECT expand(channels) FROM sample WHERE iid = :key)", params));

        params = new HashMap<>();
        params.put("key", "GSM4568913");
        stats.addValue(executeQuery("SELECT characteristics FROM (SELECT expand(channels) FROM sample WHERE iid = :key)", params));

        System.out.println("Query3," + stats.getMean() / 1000000 + " ms");
    }

    private void executeQuery4() {
        // System.out.println("Checkingquery4...");

        final SummaryStatistics stats = new SummaryStatistics();
        Map<String, Object> params = new HashMap<>();
        params.put("parameter", "treatment_raw");

        stats.addValue(executeQuery("SELECT * FROM sample WHERE :parameter in channels.characteristics.keys()", params));
        stats.addValue(executeQuery("SELECT * FROM sample WHERE :parameter in channels.characteristics.keys()", params));
        stats.addValue(executeQuery("SELECT * FROM sample WHERE :parameter in channels.characteristics.keys()", params));

        System.out.println("Query4," + stats.getMean() / 1000000 + " ms");
    }

    private void executeQuery5() {
        // System.out.println("Checkingquery5...");

        final SummaryStatistics stats = new SummaryStatistics();
        Map<String, Object> params = new HashMap<>();
        params.put("parameter", "H1");
        stats.addValue(executeQuery("SELECT * FROM sample WHERE channels.characteristics.treatment_raw = :parameter", params));

        params = new HashMap<>();
        params.put("parameter", "wild type");
        stats.addValue(executeQuery("SELECT * FROM sample WHERE channels.characteristics.genotype = :paremeter", params));

        params = new HashMap<>();
        params.put("parameter", "zebrafish neuromast hair cells");
        stats.addValue(executeQuery("SELECT * FROM sample WHERE channels.characteristics.tissue = :parameter", params));

        System.out.println("Query5," + stats.getMean() / 1000000 + " ms");
    }

    private void executeQuery6() {
        // System.out.println("Checkingquery6...");

        final SummaryStatistics stats = new SummaryStatistics();
        Map<String, Object> params = new HashMap<>();
        params.put("key", "GPL24995");
        stats.addValue(executeQuery("SELECT * FROM series WHERE platforms.iid = :key", params));

        params = new HashMap<>();
        params.put("key", "GPL14875");
        stats.addValue(executeQuery("SELECT * FROM series WHERE platforms.iid = :key", params));

        params = new HashMap<>();
        params.put("key", "GPL25922");
        stats.addValue(executeQuery("SELECT * FROM series WHERE platforms.iid = :key", params));

        System.out.println("Query6," + stats.getMean() / 1000000 + " ms");
    }

    private double executeQuery(final String query, final Map<String, Object> params) {
        return executeAndMeasureQuery(params, query);
    }

    private double executeAndMeasureQuery(final Map<String, Object> params, final String query) {
        final SummaryStatistics stats = new SummaryStatistics();
        for (int i = 0; i < ITERATIONS; i++) {
            long start = System.nanoTime();
            try (final ODatabaseSession session = odbc.getPool().acquire(); final OResultSet rs = session.query(query, params)) {
            }
            long runningTime = System.nanoTime() - start;
            stats.addValue(runningTime);
            // System.out.println(runningTime / 1000000);
        }
        // System.out.println("Mean:" + stats.getMean() / 1000000 + "Manual," + stats.getSum() / ITERATIONS);
        return stats.getMean();
    }
}
