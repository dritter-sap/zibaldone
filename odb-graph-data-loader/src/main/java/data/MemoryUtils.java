package data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Thanks go to Phil Zoio
 * (http://www.java2s.com/Code/Java/Development-Class/ReturnsusedmaxmemoryinMB.htm)
 */
public class MemoryUtils {
  private static final Logger log = LoggerFactory.getLogger(MemoryUtils.class);

  public static double usedMemoryInMB() {
    Runtime runtime = Runtime.getRuntime();
    return usedMemoryInMB(runtime);
  }

  public static double maxMemoryInMB() {
    Runtime runtime = Runtime.getRuntime();
    return maxMemoryInMB(runtime);
  }

  static double usedMemoryInMB(Runtime runtime) {
    long totalMemory = runtime.totalMemory();
    long freeMemory = runtime.freeMemory();
    double usedMemory = (double)(totalMemory - freeMemory) / (double)(1024 * 1024);
    return usedMemory;
  }

  static double maxMemoryInMB(Runtime runtime) {
    long maxMemory = runtime.maxMemory();
    double memory = (double)maxMemory / (double)(1024 * 1024);
    return memory;
  }

  public static void printMemoryInfo() {
    StringBuffer buffer = getMemoryInfo();
    log.info(buffer.toString());
  }

  public static StringBuffer getMemoryInfo() {
    StringBuffer buffer = new StringBuffer();

    freeMemory();

    Runtime runtime = Runtime.getRuntime();
    double usedMemory = usedMemoryInMB(runtime);
    double maxMemory = maxMemoryInMB(runtime);

    NumberFormat f = new DecimalFormat("###,##0.0");

    String lineSeparator = System.getProperty("line.separator");
    buffer.append("Used memory: " + f.format(usedMemory) + "MB").append(lineSeparator);
    buffer.append("Max available memory: " + f.format(maxMemory) + "MB").append(lineSeparator);
    return buffer;
  }

  public static void freeMemory() {
    System.gc();
    System.runFinalization();
  }
}
