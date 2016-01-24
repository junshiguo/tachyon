package tachyon.worker.eviction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tachyon.Pair;
import tachyon.StorageDirId;
import tachyon.thrift.MasterService.AsyncProcessor.liststatus;
import tachyon.thrift.WorkerBlockInfo;
import tachyon.worker.WorkerStorage;
import tachyon.worker.hierarchy.BlockInfo;
import tachyon.worker.hierarchy.StorageDir;

public class EvictGlobal implements EvictStrategy {
  private final boolean mLastTier;
  private final WorkerStorage mWorkerStorage;

  public EvictGlobal(boolean isLasterTier, WorkerStorage workerStorage) {
    mLastTier = isLasterTier;
    mWorkerStorage = workerStorage;
  }

  @Override
  public Pair<StorageDir, List<BlockInfo>> getDirCandidate(StorageDir[] storageDirs,
      Set<Integer> pinList, long requestBytes, long requestBlockId) {
    List<Long> lockedBlocks = new ArrayList<>();
    List<Long> storageDirIds = new ArrayList<>();
    for (StorageDir dir : storageDirs) {
      lockedBlocks.addAll(dir.getLockedBlocks());
      storageDirIds.add(dir.getStorageDirId());
    }
    try {
      Map<Long, List<WorkerBlockInfo>> evictInfo = mWorkerStorage.getBlocksToEvict(
          mWorkerStorage.getmWorkerAddress(), new HashSet<Long>(lockedBlocks), storageDirIds,
          requestBlockId, requestBytes, mLastTier);
      long choosenDirId = evictInfo.keySet().iterator().next();
      StorageDir choosenDir = mWorkerStorage.getStorageDirByDirId(choosenDirId);
      List<BlockInfo> blockInfos = new ArrayList<>();
      for (WorkerBlockInfo workerBlockInfo : evictInfo.get(choosenDirId)) {
        blockInfos.add(mWorkerStorage.getBlockInfo(workerBlockInfo));
      }
      return new Pair<StorageDir, List<BlockInfo>>(choosenDir, blockInfos);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

}
