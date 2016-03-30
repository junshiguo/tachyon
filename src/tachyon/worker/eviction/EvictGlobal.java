package tachyon.worker.eviction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import tachyon.Pair;
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
      if (pinList.contains(blockId)) {
        continue;
      }
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

}
