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

public class Test {

  public static void main(String[] args) {
    TachyonURI masterUri = new TachyonURI("tachyon://10.141.211.85:19998");
    try {
      TachyonFS fs = TachyonFS.get(masterUri);
      System.out.println("TachyonFS get!");
      // TachyonFile file = fs.getFile("/tachyon.thrift");
      // System.out.println(file);
      // fs.delete("/tachyon.thrift", false);
      fs.accessFile(new TachyonURI("/readme"));
//      copyPath(new File("tachyon.thrift"), fs, new TachyonURI("/"));
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
