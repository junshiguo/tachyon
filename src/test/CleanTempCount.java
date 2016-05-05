package test;

import java.io.IOException;

import tachyon.TachyonURI;
import tachyon.client.TachyonFS;

public class CleanTempCount {

  public static void main(String[] args) throws IOException {
    String master = "tachyon://172.31.2.206:19998";
    if (args.length > 1) {
      master = args[1];
    }
    TachyonURI masterUri = new TachyonURI(master);
    TachyonURI path = new TachyonURI(args[0]);

    TachyonFS tfs = TachyonFS.get(masterUri);
    int fileId = tfs.getFileId(path);
    tfs.clearTempBlockCount(fileId);
    tfs.close();
  }

}
