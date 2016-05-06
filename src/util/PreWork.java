package util;

import java.io.IOException;

import tachyon.TachyonURI;
import tachyon.client.TachyonFS;

public class PreWork {
  public static void main(String[] args) {
    if (args.length == 0) {
      System.err.println(
          "Parameter: <access path> [master], do access file, clean access count and clean block access infos");
      System.exit(0);
    }
    String master = "tachyon://172.31.2.206:19998";
    if (args.length > 1) {
      master = args[1];
    }
    TachyonURI masterUri = new TachyonURI(master);
    try {
      TachyonFS tfs = TachyonFS.get(masterUri);
      tfs.accessFile(new TachyonURI(args[0]));
      tfs.cleanAccessCount();
      tfs.cleanBlockAccessInfoForMaster();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
}
