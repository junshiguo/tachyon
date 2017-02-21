package util;

import java.io.IOException;

import tachyon.TachyonURI;
import tachyon.client.TachyonFS;

public class MemoryMonitor {
  public static void main(String args[]) throws IOException {
    String master = "tachyon://172.31.2.206:19998";
    if (args.length == 0) {
      System.err.println("Usage: <path|path> [master]");
      System.exit(0);
    }
    if (args.length > 1) {
      master = args[1];
    }
    TachyonURI masterUri = new TachyonURI(master);
    TachyonFS tfs = TachyonFS.get(masterUri);

    String files[] = args[0].split("|");
    for (String file : files) {
      long bytes = tfs.getMemoryConsumptionBytes(file);
      System.out.println(file + ": " + bytes + " " + bytes / 1024 / 1024 + "MB");
    }
  }
}
