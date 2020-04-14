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
  private static final Big2graphFixture fixture = new Big2graphFixture();

  @Test
  public void testParseCsv_vertices() throws Exception {
    try (final Reader in = new FileReader("src/test/resources/vertices.csv")){
      // 'getRecords()' removes the records
      final CSVParser records = CSVFormat.DEFAULT.withHeader(fixture.getVertexHeader()).parse(in);
      for (final CSVRecord record : records) {
        final String uuid_nvarchar = record.get(fixture.getVertexKey());
        Assert.assertTrue(((null != uuid_nvarchar) && (!uuid_nvarchar.isEmpty())));
      }
    }
  }

  @Test
  public void testParseCsv_edges() throws Exception {
    try (final Reader in = new FileReader("src/test/resources/edges.csv")) {
      final CSVParser records = CSVFormat.DEFAULT.withHeader(fixture.getEdgeHeader()).parse(in);

      for (final CSVRecord record : records) {
        final String start = record.get(fixture.getEdgeSource());
        Assert.assertTrue(((null != start) && (!start.isEmpty())));

        final String end = record.get(fixture.getEdgeTarget());
        Assert.assertTrue(((null != end) && (!end.isEmpty())));
      }
    }
  }
}
