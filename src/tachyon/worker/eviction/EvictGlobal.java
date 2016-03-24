package tachyon.worker.eviction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import tachyon.Pair;
import tachyon.StorageDirId;
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
      Set<Integer> pinList, long requestBytes) {
    Map<Integer, Long> memAllocation = mWorkerStorage.getMemAllocationPlan();
    if (memAllocation == null) {
      return null;
    }
    Pair<StorageDir, List<BlockInfo>> dirCandidate = null;
    for (StorageDir dir : storageDirs) {
      dirCandidate = getDirCandidate(memAllocation, dir, pinList, requestBytes);
      if (dirCandidate != null) {
        return dirCandidate;
      }
    }
    return null;
  }

  private Pair<StorageDir, List<BlockInfo>> getDirCandidate(Map<Integer, Long> memAllocation,
      StorageDir curDir, Set<Integer> pinList, long requestBytes) {
    Map<Integer, Long> fileDistribution = mWorkerStorage.getFileDistributionClone();
    Set<Entry<Long, Long>> blocks = curDir.getBlockSizes();
    long mem = curDir.getAvailableBytes();
    List<BlockInfo> toEvictBlocks = new ArrayList<>();
    for (Entry<Long, Long> entry : blocks) {
      long blockId = entry.getKey();
      long blockSize = entry.getValue();
      int fileId = tachyon.master.BlockInfo.computeInodeId(blockId);
      Long fileConsumption = fileDistribution.get(fileId);
      if (fileConsumption > memAllocation.get(fileId)) {
        mem += curDir.getBlockSize(blockSize);
        toEvictBlocks.add(new BlockInfo(curDir, blockId, blockSize));
        fileConsumption -= blockSize;
        if (fileConsumption > 0) {
          fileDistribution.put(fileId, fileConsumption);
        } else {
          fileDistribution.remove(fileId);
        }
      }

      if (mem >= requestBytes) {
        mWorkerStorage.setFileDistribution(fileDistribution);
        return new Pair<StorageDir, List<BlockInfo>>(curDir, toEvictBlocks);
      }
    }
    return null;
  }

  @Deprecated
  public Pair<StorageDir, List<BlockInfo>> getDirCandidate_masterSide(StorageDir[] storageDirs,
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
