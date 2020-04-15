package data.sorting;

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
  private static final File csvIn     = new File("src/test/resources/ldbc_vertices.csv");
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
    Assert.assertEquals(7, mergeSortedFiles);
    final BufferedReader reader = new BufferedReader(new FileReader(csvSorted));
    final String readLine = reader.readLine();
    Assert.assertEquals("comment.137438953499|||2011-06-22T01:20:39.061+0000|212.217.91.169|Internet Explorer|maybe|5||comment||||||||", readLine);
    reader.close();
  }
}
