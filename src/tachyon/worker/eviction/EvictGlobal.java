package tachyon.worker.eviction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
      Set<Integer> pinList, long requestBytes, int fileId) {
    Map<Integer, Long> memAllocation = mWorkerStorage.getMemAllocationPlan();
    if (memAllocation == null) {
      return null;
    }
    WorkerStorage.getLog().info(
        "***EvictGlobal: getMemAllocationPlan: {}, storage dir number {}***", memAllocation,
        storageDirs.length);
    Pair<StorageDir, List<BlockInfo>> dirCandidate = null;
    for (StorageDir dir : storageDirs) {
      dirCandidate = getDirCandidate(memAllocation, dir, pinList, requestBytes, fileId);
      if (dirCandidate != null) {
        return dirCandidate;
      }
    }
    return null;
  }

  private Pair<StorageDir, List<BlockInfo>> getDirCandidate(Map<Integer, Long> memAllocation,
      StorageDir curDir, Set<Integer> pinList, long requestBytes, int fileId) {
    Set<Entry<Long, Long>> blocks = curDir.getBlockSizes();
    Map<Integer, Long> fileDistribution = mWorkerStorage.getFileDistributionClone();
    WorkerStorage.getLog().info("   ###EvictGlobal: my file distribution {}###   ",
        fileDistribution);

    // Map<Integer, Long> fileDistribution1 = new HashMap<>();
    // for (Entry<Long, Long> entry : blocks) {
    // int fid = tachyon.master.BlockInfo.computeInodeId(entry.getKey());
    // Long size = fileDistribution1.get(fid);
    // if (size == null) {
    // size = 0L;
    // }
    // size += entry.getValue();
    // fileDistribution1.put(fid, size);
    // }
    // WorkerStorage.getLog().info(" ###EvictGlobal: distribution from BlockSizes {}### ",
    // fileDistribution1);

    long mem = 0;
    List<BlockInfo> toEvictBlocks = new ArrayList<BlockInfo>();
    Set<Long> blocksToEvict = new HashSet<Long>();

    while (mem < requestBytes) {
      Entry<Long, Long> entry = getBlockCandidate(memAllocation, fileDistribution, blocks, pinList,
          blocksToEvict, fileId);
      if (entry == null) {
        return null;
      }
      long blockId = entry.getKey();
      long blockSize = entry.getValue();
      mem += curDir.getBlockSize(blockId);
      toEvictBlocks.add(new BlockInfo(curDir, blockId, blockSize));
      WorkerStorage.getLog().info(
          "***EvictGlobal: candidate fileid {}, blockid {}, blocksize {}({}MB), dir {}***",
          tachyon.master.BlockInfo.computeInodeId(blockId), blockId, blockSize,
          blockSize / 1024 / 1024, curDir);

      int evictFid = tachyon.master.BlockInfo.computeInodeId(blockId);
      Long fileConsumption = fileDistribution.get(evictFid);
      if (fileConsumption != null) {
        fileConsumption -= blockSize;
        if (fileConsumption > 0) {
          fileDistribution.put(evictFid, fileConsumption);
        } else {
          fileDistribution.remove(evictFid);
        }
      }
    }
    if (mem >= requestBytes) {
      return new Pair<StorageDir, List<BlockInfo>>(curDir, toEvictBlocks);
    }
    return null;
  }

  private Entry<Long, Long> getBlockCandidate(Map<Integer, Long> memAllocation,
      Map<Integer, Long> fileDistribution, Set<Entry<Long, Long>> blocks, Set<Integer> pinList,
      Set<Long> blocksToEvict, int fileId) {
    int targetFileId = -1;
    long oversize = 0;
    for (int id : fileDistribution.keySet()) {
      if (id == fileId) {
        continue;
      }
      if (!memAllocation.containsKey(id)) {
        if (oversize < fileDistribution.get(id)) {
          oversize = fileDistribution.get(id);
          targetFileId = id;
        }
      } else {
        if (oversize < (fileDistribution.get(id) - memAllocation.get(id))) {
          oversize = fileDistribution.get(id) - memAllocation.get(id);
          targetFileId = id;
        }
      }
    }
    if (targetFileId != -1) {
      for (Entry<Long, Long> entry : blocks) {
        long blockId = entry.getKey();
        int thisFileId = tachyon.master.BlockInfo.computeInodeId(blockId);
        if (thisFileId != targetFileId || blocksToEvict.contains(blockId)
            || !blockEvictable(blockId, pinList)) {
          continue;
        }
        blocksToEvict.add(blockId);
        return entry;
      }
    }
    return null;
  }

}
