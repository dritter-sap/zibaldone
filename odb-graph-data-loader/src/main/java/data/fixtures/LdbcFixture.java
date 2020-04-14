package data.fixtures;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.IOException;
import java.io.Reader;

public class LdbcFixture implements LoadFixture {
  private static final String[] VertexHeader = {
      "id",
      "name",
      "url",
      "creationdate",
      "locationip",
      "browserused",
      "content",
      "length",
      "title",
      "type",
      "firstname",
      "lastname",
      "gender",
      "birthday",
      "imagefile",
      "language",
      "email",
      "locationtype"
  };
  
  private static final String[] EdgeHeader = {
      "source",
      "target",
      "type",
      "joindate",
      "creationdate",
      "classyear",
      "workfrom"
  };

  public String[] getVertexHeader() {
    return VertexHeader;
  }

  public String[] getEdgeHeader() {
    return EdgeHeader;
  }

  public CSVParser getCsvVertexParser(final Reader records) throws IOException {
    return CSVFormat.DEFAULT.withHeader(VertexHeader).withSkipHeaderRecord().withDelimiter('|').parse(records);
  }

  public CSVParser getCsvEdgeParser(final Reader records) throws IOException {
    return CSVFormat.DEFAULT.withHeader(EdgeHeader).withSkipHeaderRecord().withDelimiter('|').parse(records);
  }

  @Override
  public String getVertexKey() {
    return "id";
  }

  @Override
  public String getEdgeSource() {
    return "source";
  }

  @Override
  public String getEdgeTarget() {
    return "target";
  }
}
