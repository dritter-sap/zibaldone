import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.annotations.Cli;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.orientechnologies.orient.core.id.ORID;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import data.fixtures.Big2graphFixture;
import data.loaders.BatchCoordinator;
import data.loaders.GraphDataLoader;
import data.loaders.GraphDataLoaderConfig;
import data.loaders.ODBGraphDataLoader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Cli(name = "basic",
    description = "Provides a basic odb graph data loader CLI",
    defaultCommand = MassGraphDataLoader.class,
    commands = {MassGraphDataLoader.class})
@Command(name = "ODBGraphDataLoader", description = "ODB graph data loader")
public class MassGraphDataLoader {
  private static final Logger log = LoggerFactory.getLogger(MassGraphDataLoader.class);

  private static final String DATA_LOADER_PROPERTIES = "dataloader.properties";

  @Option(name = {"-user", "--user"}, description = "Database user")
  private String userName = "";

  @Option(name = {"-password", "--password"}, description = "Database password")
  private String password = "";

  @Option(name = {"-host", "--host"}, description = "Database host")
  private String host = "";

  @Option(name = {"-dbname", "--dbname"}, description = "Database name")
  private String dbname = "";

  @Option(name = {"-port", "--port"}, description = "Database port")
  private Integer port = 0;

  @Option(name = {"-vertexFileName", "--vertexFileName"}, description = "Vertex CSV file")
  private String vertexFileName = "vertices.csv";

  @Option(name = {"-edgeFileName", "--edgeFileName"}, description = "Edge CSV file")
  private String edgeFileName = "edges.csv";

  @Option(name = {"-batchSize", "--batchSize"}, description = "Batch size")
  private int batchSize = 1000;

  @Option(name = {"-numberVertices", "--numberVertices"}, description = "Expected number of vertices")
  private int numberVertices  = 10;

  @Option(name = {"-numberEdges", "--numberEdges"}, description = "Expected number of edges")
  private int numberEdges  = 10;

  public static void main(String[] args) {
    final SingleCommand<MassGraphDataLoader> parser = SingleCommand.singleCommand(MassGraphDataLoader.class);
    Arrays.asList(args).stream().forEach(p -> System.out.print(p + "##"));
    final MassGraphDataLoader cmd = parser.parse(args);
    cmd.run();
  }

  private void run() {
    final Properties props = new Properties();
    final GraphDataLoader graphDataLoader = new ODBGraphDataLoader();
    try {
      props.load(MassGraphDataLoader.class.getResourceAsStream(DATA_LOADER_PROPERTIES));
      this.mixinCmdParameterValues(props);
      final GraphDataLoaderConfig config = GraphDataLoaderConfig.load(props);
      final long start = System.currentTimeMillis();
      this.process(graphDataLoader, config, this.userName, this.password, this.vertexFileName, this.edgeFileName);
      log.debug("Process(ms) " + (System.currentTimeMillis() - start));
    } catch (final IOException e) {
      log.debug("Failed to load configuration properties " + DATA_LOADER_PROPERTIES, e);
    } catch (final Exception e) {
      log.debug("Failed to process data with data loader " + graphDataLoader, e);
    }
  }

  private void mixinCmdParameterValues(final Properties props) {
    if (props == null) {
      throw new IllegalArgumentException("'dataloader.properties' file not found.");
    }
    if (!this.host.isEmpty()) {
      props.setProperty("SERVER_NAME", this.host);
    }
    if (this.port != 0) {
      props.setProperty("SERVER_PORT", String.valueOf(this.port));
    }
    if (this.dbname.isEmpty()) {
      props.setProperty("DB_NAME", this.dbname);
    }
    if (this.batchSize != 0) {
      props.setProperty("BATCH_SIZE", String.valueOf(this.batchSize));
    }
    if (this.numberVertices != 0) {
      props.setProperty("NUMBER_VERTICES", String.valueOf(this.numberVertices));
    }
    if (this.numberEdges != 0) {
      props.setProperty("NUMBER_EDGES", String.valueOf(this.numberEdges));
    }
  }

  public void process(final GraphDataLoader dataLoader, final GraphDataLoaderConfig config, final String userName,
                      final String password, final String vertexFileName, final String edgeFileName) throws Exception {
    config.logSelectedApplicationParameters();
    final BatchCoordinator bc = new BatchCoordinator(config.getBatchSize());

    dataLoader.connect(config.getServerName(), config.getServerPort(), config.getDbName(), userName, password);
    Map<String, ORID> contextVertices = null;
    try {
      log.debug("loading vertex keys...");
      try (final Reader records = new FileReader(vertexFileName)) {
        // 'getRecords()' removes the records
        final CSVParser csvParser = CSVFormat.DEFAULT.withHeader(Big2graphFixture.VertexHeader).parse(records);
        final long start = System.currentTimeMillis();
        contextVertices = dataLoader.loadVertexKeys(csvParser, "VertexClass", Big2graphFixture.VertexHeader,
            "UUID_NVARCHAR", bc, config.getNumberVertices());
        log.debug("load vertex keys(ms) " + (System.currentTimeMillis() - start));
      }
      log.debug("loading edges...");
      try (final Reader records = new FileReader(edgeFileName)) {
        final CSVParser csvParser = CSVFormat.DEFAULT.withHeader(Big2graphFixture.EdgeHeader).parse(records);
        final long start = System.currentTimeMillis();
        dataLoader.loadEdges(csvParser, "EdgeClass", Big2graphFixture.EdgeHeader,
            "STARTUUID_NVARCHAR", "ENDUUID_NVARCHAR", contextVertices, bc,
            config.getNumberEdges());
        log.debug("load edges(ms) " + (System.currentTimeMillis() - start));
      }
      log.debug("loading vertex props...");
      try (final Reader records = new FileReader(vertexFileName)) {
        final CSVParser csvParser = CSVFormat.DEFAULT.withHeader(Big2graphFixture.VertexHeader).parse(records);
        final long start = System.currentTimeMillis();
        dataLoader.loadVertexProperties(csvParser, Big2graphFixture.VertexHeader,
            "UUID_NVARCHAR", bc, contextVertices);
        log.debug("load vertex props(ms) " + (System.currentTimeMillis() - start));
      }
      log.debug("Verify...");
      final long start = System.currentTimeMillis();
      dataLoader.verify(bc, "VertexClass", "EdgeClass", config.getNumberVertices(),
      config.getNumberEdges());
      log.debug("Verification(ms) " + (System.currentTimeMillis() - start));
    } finally {
      dataLoader.disconnect(config.getDbName());
    }
  }

  /*public void processFast(final GraphDataLoader dataLoader, final GraphDataLoaderConfig config, final String userName,
                      final String password, final String vertexFileName, final String edgeFileName) throws Exception {
    config.logSelectedApplicationParameters();
    final BatchCoordinator bc = new BatchCoordinator(config.getBatchSize());

    dataLoader.connect(config.getServerName(), config.getServerPort(), config.getDbName(), userName, password);
    Map<String, OVertex> contextVertices = null;
    try {
      log.debug("loading vertex keys...");
      try (final Reader reader = new FileReader(vertexFileName)) {
        final List<String[]> records = parseFast(reader);
        final long start = System.currentTimeMillis();
        contextVertices = dataLoader.loadVertexKeys(records, "VertexClass", Big2graphFixture.VertexHeader,
            "UUID_NVARCHAR", bc, config.getNumberVertices());
        log.debug("load vertex keys(ms) " + (System.currentTimeMillis() - start));
      }
      log.debug("loading edges...");
      try (final Reader records = new FileReader(edgeFileName)){
        final CSVParser csvParser = CSVFormat.DEFAULT.withHeader(Big2graphFixture.EdgeHeader).parse(records);
        final long start = System.currentTimeMillis();
        dataLoader.loadEdges(csvParser, "EdgeClass", Big2graphFixture.EdgeHeader,
            "STARTUUID_NVARCHAR", "ENDUUID_NVARCHAR", contextVertices, bc,
            config.getNumberEdges());
        log.debug("load edges(ms) " + (System.currentTimeMillis() - start));
      }
      log.debug("loading vertex props...");
      try (final Reader records = new FileReader(vertexFileName)) {
        // 'getRecords()' removes the records
        final CSVParser csvParser = CSVFormat.DEFAULT.withHeader(Big2graphFixture.VertexHeader).parse(records);
        final long start = System.currentTimeMillis();
        dataLoader.loadVertexProperties(csvParser, Big2graphFixture.VertexHeader,
            "UUID_NVARCHAR", bc, contextVertices);
        log.debug("load vertex props(ms) " + (System.currentTimeMillis() - start));
      }
    } finally {
      dataLoader.disconnect(config.getDbName());
    }
  }*/

  public List<String[]> parseFast(final Reader input) {
    final CsvParserSettings settings = new CsvParserSettings();
    settings.getFormat().setLineSeparator("\n");
    return new CsvParser(settings).parseAll(input);
  }
}
