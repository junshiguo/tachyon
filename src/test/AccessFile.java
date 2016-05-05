package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.google.common.io.Closer;

import tachyon.Constants;
import tachyon.TachyonURI;
import tachyon.client.OutStream;
import tachyon.client.TachyonFS;
import tachyon.client.TachyonFile;
import tachyon.conf.UserConf;

public class AccessFile {

  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Parameter: access path [master]");
      System.exit(0);
    }
    String master = "tachyon://172.31.2.206:19998";
    if (args.length > 1) {
      master = args[1];
    }
    TachyonURI masterUri = new TachyonURI(master);
    try {
      TachyonFS fs = TachyonFS.get(masterUri);
      fs.accessFile(new TachyonURI(args[0]));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  private static int copyPath(File src, TachyonFS tachyonClient, TachyonURI dstPath)
      throws IOException {
    if (!src.isDirectory()) {
      System.out.println("TFsShell.copyPath: trying getFile...");
      TachyonFile tFile = tachyonClient.getFile(dstPath);
      System.out.println("TFsShell.copyPath: getFile done!");
      if (tFile != null && tFile.isDirectory()) {
        dstPath = dstPath.join(src.getName());
      }
      int fileId = tachyonClient.createFile(dstPath);
      if (fileId == -1) {
        return -1;
      }
      System.out.println("TFsShell.copyPath: createFile done " + dstPath);
      tFile = tachyonClient.getFile(fileId);
      System.out.println("TFsShell.copyPath: get file through id " + fileId);
      Closer closer = Closer.create();
      try {
        OutStream os = closer.register(tFile.getOutStream(UserConf.get().DEFAULT_WRITE_TYPE));
        FileInputStream in = closer.register(new FileInputStream(src));
        FileChannel channel = closer.register(in.getChannel());
        ByteBuffer buf = ByteBuffer.allocate(8 * Constants.MB);
        while (channel.read(buf) != -1) {
          buf.flip();
          os.write(buf.array(), 0, buf.limit());
        }
      } finally {
        closer.close();
      }
      return 0;
    } else {
      tachyonClient.mkdir(dstPath);
      for (String file : src.list()) {
        TachyonURI newPath = new TachyonURI(dstPath, new TachyonURI(file));
        File srcFile = new File(src, file);
        if (copyPath(srcFile, tachyonClient, newPath) == -1) {
          return -1;
        }
      }
    }
    return 0;
  }

}
