package tachyon.master;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import tachyon.Pair;
import tachyon.thrift.BlockInfoException;
import tachyon.thrift.ClientBlockInfo;
import tachyon.thrift.FileDoesNotExistException;
import tachyon.thrift.NetAddress;
import tachyon.thrift.WorkerBlockInfo;

public class EvictGlobal {
  private static final long QUEUE_SIZE = 100 * 1024 * 1024 * 1024;

  /**
   * Newly added. Map from worker id to <file id, in memory bytes>. It's state should be consistent
   * with {@link tachyon.master.InodeFile}mBlocks. Updated when InodeFile.mBlocks changes.
   */
  private final ConcurrentMap<Long, ConcurrentMap<Integer, Long>> mWorkerIdToFileDistribution =
      new ConcurrentHashMap<>();
  /**
   * A map from file ID's to max in-memory block numbers. This is managed by master, looked up when
   * eviction happens and updated upon a file access.
   */
  private final Map<Integer, Long> mFileIdToMaxMem = new HashMap<>();

  private final MasterInfo mMasterInfo;
  private Long mFileQueueLength = 0L;
  private Long mBlockQueueLength = 0L;
  /**
   * file id to length
   */
  private final ConcurrentLinkedQueue<Pair<Integer, Long>> mAccessQueueFile =
      new ConcurrentLinkedQueue<>();
  private final ConcurrentMap<Integer, Long> mAccessTimeFile =
      new ConcurrentHashMap<Integer, Long>();
  /**
   * block id to length
   */
  private final ConcurrentLinkedQueue<Pair<Long, Long>> mAccessQueueBlock =
      new ConcurrentLinkedQueue<>();
  private final ConcurrentMap<Long, Long> mAccessTimeBlock = new ConcurrentHashMap<>();
  private final Map<Long, List<Long>> mWorkerIdToEvictionCandidate = new HashMap<>();

  public EvictGlobal(MasterInfo masterInfo) {
    mMasterInfo = masterInfo;
  }

  public synchronized Map<Long, List<WorkerBlockInfo>> getBlocksToEvict(NetAddress workerAddress,
      Set<Long> lockedBlocks, List<Long> candidateDirIds, long blockId, long requestBytes,
      boolean isLastTier) {
    List<WorkerBlockInfo> toEvictBlockInfos = new ArrayList<>();
    List<Long> toEvictBlockIds = new ArrayList<>();
    Map<Long, Long> sizeToEvict = new HashMap<Long, Long>(); // storage id to size
    Long retDirId = null;
    Long workerId = mMasterInfo.getmWorkerAddressToId().get(workerAddress);
    if (workerId == null) {
      MasterInfo.getLog().error("worker_getBlocksToEvict: no worker {} exist.", workerAddress);
      return null;
    }

    updateLRUCandidate();

    // first, add eviction candidates to the toEvict list
    synchronized (mWorkerIdToEvictionCandidate) {
      if (mWorkerIdToEvictionCandidate.containsKey(workerId)) {
        mWorkerIdToEvictionCandidate.get(workerId).removeAll(lockedBlocks);
        toEvictBlockIds.addAll(mWorkerIdToEvictionCandidate.get(workerId));
        mWorkerIdToEvictionCandidate.remove(workerId);
      }
    }
    if (toEvictBlockIds.isEmpty() == false) {
      for (long id : toEvictBlockIds) {
        try {
          WorkerBlockInfo workerBlockInfo = mMasterInfo.getWorkerBlockInfo(id, workerAddress);
          toEvictBlockInfos.add(workerBlockInfo);
          long evictBytes;
          if (sizeToEvict.containsKey(workerBlockInfo.storageDirId)) {
            evictBytes = sizeToEvict.get(workerBlockInfo.storageDirId) + workerBlockInfo.blockSize;
          } else {
            evictBytes = workerBlockInfo.blockSize;
          }
          sizeToEvict.put(workerBlockInfo.storageDirId, evictBytes);
          if (sizeToEvict.get(workerBlockInfo.storageDirId) >= requestBytes
              && candidateDirIds.contains(workerBlockInfo.storageDirId)) {
            retDirId = workerBlockInfo.storageDirId;
          }
        } catch (FileDoesNotExistException e) {
          MasterInfo.getLog().error(e.getMessage());
        } catch (BlockInfoException e) {
          MasterInfo.getLog().error(e.getMessage());
        }
      }
    }
    Map<Long, List<WorkerBlockInfo>> ret = new HashMap<>();
    // if bytes evicted from the candidate list is already enough, return
    if (retDirId != null) {
      ret.put(retDirId, toEvictBlockInfos);
      return ret;
    }
    // use global info: pinlist, memory consumption of each file, allocated space of each file
    List<Integer> pinList = mMasterInfo.getPinIdList();
    ConcurrentMap<Integer, Long> fileDistribution = mWorkerIdToFileDistribution.get(workerId);
    MasterWorkerInfo workerInfo = mMasterInfo.getWorkerInfo(workerId);
    while (true) {
      for (Integer fileId : fileDistribution.keySet()) {
        // if (mMasterInfo.get)
      }
      break;
    }
    // TODO: evict info
    return null;
  }

  public void updateLRUCandidate() {
    // TODO: Use file or use block, it is a question.
  }

  /**
   * Update mWorkerIdToEvictionCandidate based on file access queue. Put all blocks of the file
   * polled out of queue in the candidate list.
   * 
   * @throws FileDoesNotExistException
   */
  public void updateLRUCandidateFile() throws FileDoesNotExistException {
    synchronized (mWorkerIdToEvictionCandidate) {
      synchronized (mAccessQueueFile) {
        while (mFileQueueLength > QUEUE_SIZE) {
          Pair<Integer, Long> file = mAccessQueueFile.poll();
          if (file == null) {
            MasterInfo.getLog()
                .debug("EvictGlobal.updsateLRUCandidateFile: mAccessQueueFile is empty");
            return;
          }
          mFileQueueLength -= file.getSecond();
          if (mAccessTimeFile.get(file.getFirst()) > file.getSecond()) {
            continue;
          }
          List<ClientBlockInfo> clientBlockInfos = mMasterInfo.getFileBlocks(file.getFirst());
          for (ClientBlockInfo blockInfo : clientBlockInfos) {
            for (NetAddress address : blockInfo.getLocations()) {
              if (address.mSecondaryPort == -1) {
                continue;
              }
              long workerId = mMasterInfo.getmWorkerAddressToId().get(address);
              if (mWorkerIdToEvictionCandidate.containsKey(workerId) == false) {
                mWorkerIdToEvictionCandidate.put(workerId, new ArrayList<Long>());
              }
              mWorkerIdToEvictionCandidate.get(workerId).add(blockInfo.getBlockId());
            }
          }
        }
      }
    }

  }

  /**
   * Update mWorkerIdToEvictionCandidate based on block access queue.
   * 
   * @throws FileDoesNotExistException
   * @throws BlockInfoException
   */
  public void updateLRUCandidateBlock() throws FileDoesNotExistException, BlockInfoException {
    synchronized (mWorkerIdToEvictionCandidate) {
      synchronized (mAccessQueueBlock) {
        while (mBlockQueueLength > QUEUE_SIZE) {
          Pair<Long, Long> block = mAccessQueueBlock.poll();
          if (block == null) {
            MasterInfo.getLog()
                .debug("EvictGlobal.updateLRUCandidateBlock: mAccessQueueBlock is empty");
            return;
          }
          mBlockQueueLength -= block.getSecond();
          if (mAccessTimeBlock.get(block.getFirst()) > block.getSecond()) {
            continue;
          }
          ClientBlockInfo blockInfo = mMasterInfo.getClientBlockInfo(block.getFirst());
          for (NetAddress address : blockInfo.getLocations()) {
            if (address.mSecondaryPort == -1) {
              continue;
            }
            long workerId = mMasterInfo.getmWorkerAddressToId().get(address);
            if (mWorkerIdToEvictionCandidate.containsKey(workerId) == false) {
              mWorkerIdToEvictionCandidate.put(workerId, new ArrayList<Long>());
            }
            mWorkerIdToEvictionCandidate.get(workerId).add(blockInfo.getBlockId());
          }
        }
      }
    }
  }

  public void updateMaxMemPerFile() {
    // TODO:
  }

  public void updateEvictionCandidate(boolean add, long workerId, Collection<Long> list) {
    synchronized (mWorkerIdToEvictionCandidate) {
      if (mWorkerIdToEvictionCandidate.containsKey(workerId)) {
        if (add) {
          mWorkerIdToEvictionCandidate.get(workerId).addAll(list);
        } else {
          mWorkerIdToEvictionCandidate.get(workerId).removeAll(list);
        }
      }
    }
    MasterInfo.getLog().debug("EvictGlobal: updateEvictionCandidate: {}, {}, {}", add, workerId,
        list);
  }

  public void accessFile(int fileId) {
    Inode inode = mMasterInfo.getmFileIdToInodes().get(fileId);
    if (inode == null || inode.isDirectory()) {
      MasterInfo.getLog().error("EvictGlobal.accessFile: {} is not a file.", inode);
      return;
    }
    long length = ((InodeFile) inode).getLength();
    mAccessQueueFile.add(new Pair<Integer, Long>(fileId, length));
    mAccessTimeFile.put(fileId, length);
    mFileQueueLength += length;
  }

  public void accessBlock(long blockId) {
    try {
      long length = mMasterInfo.getClientBlockInfo(blockId).length;
      mAccessQueueBlock.add(new Pair<Long, Long>(blockId, length));
      mAccessTimeBlock.put(blockId, length);
      mBlockQueueLength += length;
    } catch (FileDoesNotExistException e) {
      MasterInfo.getLog().error(e.getMessage());
    } catch (BlockInfoException e) {
      MasterInfo.getLog().error(e.getMessage());
    }
  }

  /**
   * Update eviction candidate when worker do heartbeat. Remove already removed blocks.
   * 
   * @param workerId
   * @param blockIds should be removedBlocks in
   *        {@link tachyon.master.MasterInfo#workerHeartbeat(long, long, List, Map)}.
   */
  public void filterEvictionCandidate(long workerId, List<Long> blockIds) {
    updateEvictionCandidate(false, workerId, blockIds);
  }

}
