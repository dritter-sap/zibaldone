package data.loaders;

import data.fixtures.Big2graphFixture;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileReader;
import java.io.Reader;

public class TestODBGraphDataLoader {
  @Test
  public void testParseCsv_vertices() throws Exception {
    try (final Reader in = new FileReader("src/test/resources/vertices.csv")){
      // 'getRecords()' removes the records
      final CSVParser records = CSVFormat.DEFAULT.withHeader(Big2graphFixture.VertexHeader).parse(in);
      for (final CSVRecord record : records) {
        final String uuid_nvarchar = record.get("UUID_NVARCHAR");
        Assert.assertTrue(((null != uuid_nvarchar) && (!uuid_nvarchar.isEmpty())));
      }
    }
  }

  @Test
  public void testParseCsv_edges() throws Exception {
    try (final Reader in = new FileReader("src/test/resources/edges.csv")) {
      final CSVParser records = CSVFormat.DEFAULT.withHeader(Big2graphFixture.EdgeHeader).parse(in);

      for (final CSVRecord record : records) {
        final String start = record.get("STARTUUID_NVARCHAR");
        Assert.assertTrue(((null != start) && (!start.isEmpty())));

        final String end = record.get("ENDUUID_NVARCHAR");
        Assert.assertTrue(((null != end) && (!end.isEmpty())));
      }
    }
  }
}
