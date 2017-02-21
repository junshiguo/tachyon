package test;

import java.io.IOException;

import tachyon.PrefixList;
import tachyon.TachyonURI;
import tachyon.client.TachyonFS;
import tachyon.util.UfsUtils;

public class OpenHdfsFile {

  public static void main(String[] args) throws IOException {
    String master = "tachyon://172.31.2.206:19998";
    if (args.length > 1) {
      master = args[1];
    }
    TachyonURI masterUri = new TachyonURI(master);
    TachyonURI filepath = new TachyonURI(args[0]);

    TachyonFS tfs = TachyonFS.get(masterUri);
    String mUnderFSAddress = tfs.getUfsAddress();
    if (!tfs.exist(filepath)) {
      TachyonURI ufsUri = new TachyonURI(mUnderFSAddress);
      TachyonURI ufsAddrPath =
          new TachyonURI(ufsUri.getScheme(), ufsUri.getAuthority(), filepath.getPath());
      UfsUtils.loadUnderFs(tfs, filepath.getParent(), ufsAddrPath, new PrefixList(null));
    }
  }

}
