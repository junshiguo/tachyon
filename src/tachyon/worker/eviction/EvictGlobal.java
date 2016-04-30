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

  private boolean blockEvictable(long blockId, Set<Integer> pinList) {
    return !mLastTier || !pinList.contains(tachyon.master.BlockInfo.computeInodeId(blockId));
  }

  @Override
  public synchronized Pair<StorageDir, List<BlockInfo>> getDirCandidate(StorageDir[] storageDirs,
      Set<Integer> pinList, long requestBytes) {
    Map<Integer, Long> memAllocation = mWorkerStorage.getMemAllocationPlan();
    if (memAllocation == null) {
      return null;
    }
    WorkerStorage.getLog().info("***EvictGlobal: getMemAllocationPlan: {}***", memAllocation);
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
    List<BlockInfo> toEvictBlocks = new ArrayList<BlockInfo>();
    for (Entry<Long, Long> entry : blocks) {
      long blockId = entry.getKey();
      if (!blockEvictable(blockId, pinList)) {
        continue;
      }
      long blockSize = entry.getValue();
      int fileId = tachyon.master.BlockInfo.computeInodeId(blockId);
      Long fileConsumption = fileDistribution.get(fileId);
      if (fileConsumption == null || fileConsumption
          + mWorkerStorage.getFileTempMaxBytes(fileId) > memAllocation.get(fileId)) {
        mem += curDir.getBlockSize(blockId);
        toEvictBlocks.add(new BlockInfo(curDir, blockId, blockSize));
        WorkerStorage.getLog().info(
            "***EvictGlobal: candidate fileid {}, blockid {}, blocksize {}({}MB), dir {}***",
            fileId, blockId, blockSize, blockSize / 1024 / 1024, curDir);
        if (fileConsumption != null) {
          fileConsumption -= blockSize;
          if (fileConsumption > 0) {
            fileDistribution.put(fileId, fileConsumption);
          } else {
            fileDistribution.remove(fileId);
          }
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
