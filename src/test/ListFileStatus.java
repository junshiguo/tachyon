package test;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import tachyon.TachyonURI;
import tachyon.client.TachyonFS;
import tachyon.thrift.ClientFileInfo;
import tachyon.thrift.UserBlockAccessInfo;

public class ListFileStatus {

  public static void main(String args[]) throws IOException {
    String master = "tachyon://172.31.2.206:19998";
    if (args.length == 0) {
      System.err.println("Usage: <path> [master]");
      System.exit(0);
    }
    if (args.length > 1) {
      master = args[1];
    }
    TachyonURI masterUri = new TachyonURI(master);

    TachyonFS tfs = TachyonFS.get(masterUri);
    List<ClientFileInfo> files = tfs.listStatus(new TachyonURI(args[0]));
    for (ClientFileInfo fileInfo : files) {
      System.out.println(fileInfo.name + " " + fileInfo.inMemoryPercentage);
    }
    System.out.println("##########");
    Set<UserBlockAccessInfo> infos = tfs.getBlockAccessInfoFromMaster();
    for (UserBlockAccessInfo info : infos) {
      System.out.println(info.toString());
    }
  }

}
