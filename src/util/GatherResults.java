package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tachyon.TachyonURI;
import tachyon.client.TachyonFS;
import tachyon.thrift.ClientFileInfo;
import tachyon.thrift.UserBlockAccessInfo;

public class GatherResults {
  public static void main(String[] args) throws IOException {
    if (args.length < 2) {
      System.err.println("Usage: ... <output path> <test data folder> [index] [tachyon master]");
      System.exit(0);
    }
    int index = -1;
    String master = "tachyon://172.31.2.206:19998";
    if (args.length > 3) {
      master = args[3];
    }
    if (args.length > 2) {
      index = Integer.parseInt(args[2]);
    }
    TachyonURI masterUri = new TachyonURI(master);
    TachyonFS tfs = TachyonFS.get(masterUri);
    String outpath = args[0];
    BufferedWriter writer = new BufferedWriter(new FileWriter(outpath, true));
    writer.write("###START\t" + index + "###\n");

    List<Integer> list = tfs.getAccessCount();
    writer.write("#access\thit\tmiss\treadRemote\treadUfs\n");
    writer.write(list.get(0) + "\t" + list.get(1) + "\t" + list.get(2) + "\t" + list.get(3) + "\t"
        + list.get(4) + "\n");

    writer.write("#memory percent and consumption\n");
    List<ClientFileInfo> files = tfs.listStatus(new TachyonURI(args[1]));
    for (ClientFileInfo fileInfo : files) {
      writer.write(fileInfo.name + "\t" + fileInfo.inMemoryPercentage + "\t"
          + tfs.getMemoryConsumptionBytes(fileInfo.path));
      writer.newLine();
    }

    Set<UserBlockAccessInfo> infos = tfs.getBlockAccessInfoFromMaster();
    Set<Long> blockIds = new HashSet<Long>();
    writer.write("#details\t" + infos.size() + "\n");
    for (UserBlockAccessInfo info : infos) {
      blockIds.add(info.blockId);
      writer.write(info.toString());
      writer.newLine();
    }

    writer.write("##details\t" + infos.size() + "\t" + blockIds.size() + "\n");
    for (UserBlockAccessInfo info : infos) {
      writer.write(info.fileId + "\t" + info.blockId + "\t" + info.sizeByte + "\t" + info.openTimeMs
          + "\t" + info.closeTimeMs + "\t" + info.duration + "\t" + info.readSource);
      writer.newLine();
    }

    writer.write("###END###");
    writer.close();
  }
}
