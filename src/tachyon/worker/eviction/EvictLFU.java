package tachyon.worker.eviction;

import java.util.List;
import java.util.Set;

import tachyon.Pair;
import tachyon.worker.hierarchy.BlockInfo;
import tachyon.worker.hierarchy.StorageDir;

/**
 * only support one storage dir condition
 * 
 * @author guojunshi
 *
 */
public class EvictLFU implements EvictStrategy {

  @Override
  public Pair<StorageDir, List<BlockInfo>> getDirCandidate(StorageDir[] storageDirs,
      Set<Integer> pinList, long requestBytes, int fileId) {
    // TODO Auto-generated method stub
    return null;
  }

}
