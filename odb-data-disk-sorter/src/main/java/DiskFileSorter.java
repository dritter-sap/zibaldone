import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.annotations.Cli;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import data.sorting.CsvExternalSort;
import data.sorting.CsvSortOptions;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Cli(name = "basic",
    description = "Provides a basic odb graph data loader CLI",
    defaultCommand = DiskFileSorter.class,
    commands = {DiskFileSorter.class})
@Command(name = "DiskFileSorter", description = "Disk-based File Sorter")
public class DiskFileSorter {
  private static final Logger log = LoggerFactory.getLogger(DiskFileSorter.class);

  @Option(name = {"-inputFileName", "--inputFileName"}, description = "Input file name")
  private String inputFileName = "data.csv";

  public static void main(String[] args) {
    final SingleCommand<DiskFileSorter> parser = SingleCommand.singleCommand(DiskFileSorter.class);
    Arrays.asList(args).stream().forEach(p -> System.out.print(p + "##"));
    final DiskFileSorter cmd = parser.parse(args);
    cmd.run();
  }

  private void run() {
    final DiskFileSorter sorter = new DiskFileSorter();

    try {
      final Comparator<CSVRecord> comparator = (op1, op2) -> op1.get(0).compareTo(op2.get(0));

      final long start = System.currentTimeMillis();
      final List<File> tmpFiles = this.process(comparator, this.inputFileName);
      log.debug("Process(ms) " + (System.currentTimeMillis() - start));
      log.debug(tmpFiles.get(0).getAbsolutePath());
    } catch (final IOException e) {
      log.debug("Failed to load file " + this.inputFileName, e);
    } catch (final Exception e) {
      log.debug("Failed to process data with sorter " + sorter, e);
    }
  }

  private List<File> process(final Comparator<CSVRecord> comparator, final String inputFileName) throws IOException {
    final CsvSortOptions sortOptions = new CsvSortOptions
        .Builder(CsvExternalSort.DEFAULTMAXTEMPFILES, comparator, 1, CsvExternalSort.estimateAvailableMemory())
        .charset(Charset.defaultCharset())
        .distinct(false)
        .numHeader(1)
        .skipHeader(true)
        .format(CSVFormat.DEFAULT)
        .build();
    final long start = System.currentTimeMillis();
    final List<File> tmpFiles = CsvExternalSort.sortInBatch(new File(inputFileName), null, sortOptions);
    log.debug("Sorting(ms) " + (System.currentTimeMillis() - start));
    return tmpFiles;
  }
}
