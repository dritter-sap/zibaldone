package data.sorting;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.nio.charset.Charset;
import java.util.Comparator;

public class CsvSortOptions {
  private final long dataLength;
  private final Comparator<CSVRecord> comparator;
  private final int maxTmpFiles;
  private final long maxMemory;
  private final Charset charset;

  private final boolean distinct;
  private final int numHeader; //number of header row in input file
  private final boolean skipHeader; //print header or not to output file
  private final CSVFormat format;

  public long getDataLength() {
    return dataLength;
  }

  public Comparator<CSVRecord> getComparator() {
    return comparator;
  }

  public int getMaxTmpFiles() {
    return maxTmpFiles;
  }

  public long getMaxMemory() {
    return maxMemory;
  }

  public Charset getCharset() {
    return charset;
  }

  public boolean isDistinct() {
    return distinct;
  }

  public int getNumHeader() {
    return numHeader;
  }

  public boolean isSkipHeader() {
    return skipHeader;
  }

  public CSVFormat getFormat() {
    return format;
  }

  public static class Builder {
    //mandatory params
    private final long datalength;
    private final Comparator<CSVRecord> cmp;
    private final int maxTmpFiles;
    private final long maxMemory;

    //optional params with default values
    private Charset cs = Charset.defaultCharset();
    private boolean distinct = false;
    private int numHeader = 0;
    private boolean skipHeader = true;
    private CSVFormat format = CSVFormat.DEFAULT;

    public Builder(long datalength, Comparator<CSVRecord> cmp, int maxTmpFiles, long maxMemory) {
      this.datalength = datalength;
      this.cmp = cmp;
      this.maxTmpFiles = maxTmpFiles;
      this.maxMemory = maxMemory;
    }

    public Builder charset(Charset value){
      cs = value;
      return this;
    }

    public Builder distinct(boolean value){
      distinct = value;
      return this;
    }

    public Builder numHeader(int value){
      numHeader = value;
      return this;
    }

    public Builder skipHeader(boolean value){
      skipHeader = value;
      return this;
    }

    public Builder format(CSVFormat value){
      format = value;
      return this;
    }


    public CsvSortOptions build(){
      return new CsvSortOptions(this);
    }
  }

  private CsvSortOptions(Builder builder){
    this.dataLength = builder.datalength;
    this.comparator = builder.cmp;
    this.maxTmpFiles = builder.maxTmpFiles;
    this.maxMemory = builder.maxMemory;
    this.charset = builder.cs;
    this.distinct = builder.distinct;
    this.numHeader = builder.numHeader;
    this.skipHeader = builder.skipHeader;
    this.format = builder.format;
  }
}
