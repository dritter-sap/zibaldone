package data.preparation;

import com.google.code.externalsorting.ExternalSort;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.List;

public class TestExternalSorting {
  private static final File csvIn     = new File("src/test/resources/vertices.csv");
  private static final File csvSorted = new File("target/vertices_sorted.csv");

  final File stringsIn     = new File("src/test/resources/strings.txt");
  final File stringsSorted = new File("target/strings_sorted.txt");

  @Test
  public void testStrings() throws Exception {
    ExternalSort.mergeSortedFiles(ExternalSort.sortInBatch(stringsIn), stringsSorted);
  }

  @Test
  public void testCsv() throws Exception {
    final Comparator<CSVRecord> comparator = (op1, op2) -> op1.get(0).compareTo(op2.get(0));
    final CsvSortOptions sortOptions = new CsvSortOptions
        .Builder(CsvExternalSort.DEFAULTMAXTEMPFILES, comparator, 1, CsvExternalSort.estimateAvailableMemory())
        .charset(Charset.defaultCharset())
        .distinct(false)
        // .numHeader(1)
        // .skipHeader(true)
        .format(CSVFormat.DEFAULT)
        .build();
    final List<File> sortInBatch = CsvExternalSort.sortInBatch(csvIn, null, sortOptions);
    Assert.assertEquals(1, sortInBatch.size());
    final int mergeSortedFiles = CsvExternalSort.mergeSortedFiles(sortInBatch, csvSorted, sortOptions, true);
    Assert.assertEquals(10, mergeSortedFiles);
    final BufferedReader reader = new BufferedReader(new FileReader(csvSorted));
    final String readLine = reader.readLine();
    Assert.assertEquals("107901,,,0d846390-d93d-11e4-a891-53b449010d08,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,NULL", readLine);
    reader.close();
  }
}
