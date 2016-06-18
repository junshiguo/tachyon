package tachyon.worker.eviction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import tachyon.Pair;
import tachyon.worker.WorkerStorage;
import tachyon.worker.hierarchy.BlockInfo;
import tachyon.worker.hierarchy.StorageDir;

/**
 * only support one storage dir circumstance
 * 
 * @author guojunshi
 *
 */
public class EvictMaxMin implements EvictStrategy {
  private final boolean mLastTier;
  private final WorkerStorage mWorkerStorage;

  public EvictMaxMin(boolean isLastTier, WorkerStorage workerStorage) {
    mLastTier = isLastTier;
    mWorkerStorage = workerStorage;
  }

  private boolean blockEvictable(long blockId, Set<Integer> pinList) {
    return !mLastTier || !pinList.contains(tachyon.master.BlockInfo.computeInodeId(blockId));
  }

  @Override
  public synchronized Pair<StorageDir, List<BlockInfo>> getDirCandidate(StorageDir[] storageDirs,
      Set<Integer> pinList, long requestBytes, int fileId) {
    for (StorageDir dir : storageDirs) {
      Pair<StorageDir, List<BlockInfo>> ret = getDirCandidate(dir, pinList, requestBytes, fileId);
      if (ret != null) {
        return ret;
      }
    }
    return null;
  }

  private Pair<StorageDir, List<BlockInfo>> getDirCandidate(StorageDir dir, Set<Integer> pinList,
      long requestBytes, int fileId) {
    Map<Integer, Long> fileDistribution = dir.getFileConsumptionClone();
    Set<Entry<Long, Long>> blocks = dir.getBlockSizes();
    List<BlockInfo> toEvictBlocks = new ArrayList<>();
    Set<Long> toEvictBlockIds = new HashSet<>();
    long evictBytes = 0;
    while (evictBytes < requestBytes) {
      int candidateFileId = getMaxFile(fileDistribution);
      if (candidateFileId == -1) {
        return null;
      }

      // long candidateBlockId = getLruForFile(dir, candidateFileId, toEvictBlockIds, pinList);
      // if (candidateBlockId == -1) {
      // return null;
      // }
      // long candidateBlockSize = dir.getBlockSize(candidateBlockId);
      // toEvictBlockIds.add(candidateBlockId);
      // toEvictBlocks.add(new BlockInfo(dir, candidateBlockId, candidateBlockSize));
      // evictBytes += candidateBlockSize;
      // Long fileComsumption = fileDistribution.get(candidateFileId);
      // if (fileComsumption != null) {
      // fileComsumption -= candidateBlockSize;
      // fileDistribution.put(candidateFileId, fileComsumption);
      // }

      //
      int step = new Random(System.currentTimeMillis()).nextInt(10);
      for (Entry<Long, Long> entry : blocks) {
        long candidateBlockId = entry.getKey();
        long candidateBlockSize = entry.getValue();
        if (dir.containsBlock(candidateBlockId) && blockEvictable(candidateBlockId, pinList)
            && !toEvictBlockIds.contains(candidateBlockId)
            && tachyon.master.BlockInfo.computeInodeId(candidateBlockId) == candidateFileId) {
          if (step -- > 0) {
            continue;
          }
          toEvictBlockIds.add(candidateBlockId);
          toEvictBlocks.add(new BlockInfo(dir, candidateBlockId, candidateBlockSize));
          evictBytes += candidateBlockSize;
          Long fileComsumption = fileDistribution.get(candidateFileId);
          if (fileComsumption != null) {
            fileComsumption -= candidateBlockSize;
            fileDistribution.put(candidateFileId, fileComsumption);
          }
          break;
        }
      }
    }
    return new Pair<StorageDir, List<BlockInfo>>(dir, toEvictBlocks);
  }

  private long getLruForFile(StorageDir dir, int fileId, Set<Long> toEvictBlockIds,
      Set<Integer> pinList) {
    long blockId = -1;
    long oldestTime = Long.MAX_VALUE;
    Set<Entry<Long, Long>> accessTimes = dir.getLastBlockAccessTimeMs();

    for (Entry<Long, Long> accessTime : accessTimes) {
      if (toEvictBlockIds.contains(accessTime.getKey())
          || tachyon.master.BlockInfo.computeInodeId(accessTime.getKey()) != fileId) {
        continue;
      }
      if (accessTime.getValue() < oldestTime && !dir.isBlockLocked(accessTime.getKey())) {
        if (blockEvictable(accessTime.getKey(), pinList)) {
          oldestTime = accessTime.getValue();
          blockId = accessTime.getKey();
        }
      }
    }

    return blockId;
  }

  private int getMaxFile(Map<Integer, Long> fileDistribution) {
    int fileId = -1;
    long consumption = 0;
    for (Entry<Integer, Long> entry : fileDistribution.entrySet()) {
      if (entry.getValue() > consumption) {
        consumption = entry.getValue();
        fileId = entry.getKey();
      }
    }
    return fileId;
  }

}
