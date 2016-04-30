package test;

import java.io.IOException;

import tachyon.PrefixList;
import tachyon.TachyonURI;
import tachyon.client.TachyonFS;
import tachyon.util.UfsUtils;

public class OpenHdfsFile {

  public static void main(String[] args) throws IOException {
    TachyonURI masterUri = new TachyonURI("tachyon://10.141.211.85:19998");
    TachyonURI filepath = new TachyonURI(args[0]);

    TachyonFS tfs = TachyonFS.get(masterUri);
    String mUnderFSAddress = tfs.getUfsAddress();
    if (!tfs.exist(filepath)) {
      TachyonURI ufsUri = new TachyonURI(mUnderFSAddress);
      TachyonURI ufsAddrPath =
          new TachyonURI(ufsUri.getScheme(), ufsUri.getAuthority(), filepath.getPath());
      UfsUtils.loadUnderFs(tfs, filepath, ufsAddrPath, new PrefixList(null));
    }
  }

}
