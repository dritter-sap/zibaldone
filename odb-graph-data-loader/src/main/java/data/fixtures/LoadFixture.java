package data.fixtures;

import org.apache.commons.csv.CSVParser;

import java.io.IOException;
import java.io.Reader;

public interface LoadFixture {
  String[] getVertexHeader();
  String[] getEdgeHeader();

  CSVParser getCsvVertexParser(final Reader records) throws IOException;
  CSVParser getCsvEdgeParser(final Reader records) throws IOException;

  String getVertexKey();

  String getEdgeSource();
  String getEdgeTarget();
}
