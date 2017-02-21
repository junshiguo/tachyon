package tachyon.worker.eviction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.HashMultimap;

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
  private final boolean mLastTier;

  public EvictLFU(boolean lastTier) {
    mLastTier = lastTier;
  }

  private boolean blockEvictable(long blockId, Set<Integer> pinList) {
    return !mLastTier || !pinList.contains(tachyon.master.BlockInfo.computeInodeId(blockId));
  }

  @Override
  public synchronized Pair<StorageDir, List<BlockInfo>> getDirCandidate(StorageDir[] storageDirs,
      Set<Integer> pinList, long requestBytes, int fileId) {
    List<BlockInfo> blockInfoList = new ArrayList<BlockInfo>();
    Map<StorageDir, Pair<Long, Integer>> dir2LFUBlocks =
        new HashMap<StorageDir, Pair<Long, Integer>>();
    HashMultimap<StorageDir, Long> dir2BlocksToEvict = HashMultimap.create();
    Map<StorageDir, Long> sizeToEvict = new HashMap<StorageDir, Long>();
    while (true) {
      Pair<StorageDir, Long> candidate =
          getLFUBlockCandidate(storageDirs, dir2LFUBlocks, dir2BlocksToEvict, pinList);
      StorageDir dir = candidate.getFirst();
      if (dir == null) {
        return null;
      }
      long blockId = candidate.getSecond();
      long blockSize = dir.getBlockSize(blockId);
      blockInfoList.add(new BlockInfo(dir, blockId, blockSize));
      dir2BlocksToEvict.put(dir, blockId);
      dir2LFUBlocks.remove(dir);
      long evictBytes;
      // Update eviction size for this StorageDir
      if (sizeToEvict.containsKey(dir)) {
        evictBytes = sizeToEvict.get(dir) + blockSize;
      } else {
        evictBytes = blockSize;
      }
      sizeToEvict.put(dir, evictBytes);
      if (evictBytes + dir.getAvailableBytes() >= requestBytes) {
        return new Pair<StorageDir, List<BlockInfo>>(dir, blockInfoList);
      }
    }
  }

  private Pair<StorageDir, Long> getLFUBlockCandidate(StorageDir[] storageDirs,
      Map<StorageDir, Pair<Long, Integer>> dir2LFUBlocks,
      HashMultimap<StorageDir, Long> dir2BlocksToEvict, Set<Integer> pinList) {
    StorageDir dirCandidate = null;
    long blockId = -1;
    int leastCount = Integer.MAX_VALUE;
    for (StorageDir dir : storageDirs) {
      Pair<Long, Integer> lfuBlock;
      if (!dir2LFUBlocks.containsKey(dir)) {
        lfuBlock = getLFUBlock(dir, dir2BlocksToEvict.get(dir), pinList);
        if (lfuBlock.getFirst() != -1) {
          dir2LFUBlocks.put(dir, lfuBlock);
        } else {
          continue;
        }
      } else {
        lfuBlock = dir2LFUBlocks.get(dir);
      }
      if (lfuBlock.getSecond() < leastCount) {
        blockId = lfuBlock.getFirst();
        leastCount = lfuBlock.getSecond();
        dirCandidate = dir;
      }
    }
    return new Pair<StorageDir, Long>(dirCandidate, blockId);
  }

  private Pair<Long, Integer> getLFUBlock(StorageDir dir, Set<Long> toEvictBlockIds,
      Set<Integer> pinList) {
    long blockId = -1;
    int leastAccessCount = Integer.MAX_VALUE;
    long lastAccessTime = -1;
    Set<Entry<Long, Integer>> blockAccessCount = dir.getBlockAccessCount();

    for (Entry<Long, Integer> entry : blockAccessCount) {
      if (toEvictBlockIds.contains(entry.getKey()) || !dir.containsBlock(entry.getKey())) {
        continue;
      }
      long curBlockId = entry.getKey();
      int curCount = entry.getValue();
      long time = dir.getLastBlockAccessTimeMs(curBlockId);
      if (time != -1 && !dir.isBlockLocked(curBlockId) && (curCount < leastAccessCount
          || curCount == leastAccessCount && time < lastAccessTime)) {
        if (blockEvictable(curBlockId, pinList)) {
          blockId = curBlockId;
          leastAccessCount = curCount;
          lastAccessTime = time;
        }
      }
    }
    return new Pair<Long, Integer>(blockId, leastAccessCount);
  }

}
