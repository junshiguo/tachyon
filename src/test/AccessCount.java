package test;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.ls.LSInput;

import tachyon.TachyonURI;
import tachyon.client.TachyonFS;

public class AccessCount {
  public static void main(String[] args) {
    String master = "tachyon://172.31.2.206:19998";
    if (args.length > 1) {
      master = args[1];
    }
    TachyonURI masterUri = new TachyonURI(master);
    if (args[0].equals("get")) {
      showAccessCount(masterUri);
    } else if (args[0].equals("clean")) {
      cleanAccessCount(masterUri);
    }
  }

  public static void showAccessCount(TachyonURI masterUri) {
    try {
      TachyonFS fs = TachyonFS.get(masterUri);
      List<Integer> list = fs.getAccessCount();
      System.out.println("Access: " + list.get(0) + ", miss: " + list.get(1) + ", hit: "
          + (list.get(0) - list.get(1)));
      System.out.println("Read remote: " + list.get(2) + ", read ufs: " + list.get(3));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static void cleanAccessCount(TachyonURI masterUri) {
    try {
      TachyonFS fs = TachyonFS.get(masterUri);
      fs.cleanAccessCount();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
