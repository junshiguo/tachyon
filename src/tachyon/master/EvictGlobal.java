package tachyon.master;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.hsqldb.lib.HashSet;

import tachyon.Pair;
import tachyon.thrift.BlockInfoException;
import tachyon.thrift.ClientBlockInfo;
import tachyon.thrift.FileDoesNotExistException;
import tachyon.thrift.MasterService.AsyncProcessor.liststatus;
import tachyon.thrift.NetAddress;
import tachyon.thrift.WorkerBlockInfo;

public class EvictGlobal {
  private static long QUEUE_SIZE = 100 * 1024 * 1024 * 1024;
  /**
   * A map from file ID's to max in-memory block numbers. This is managed by master, looked up when
   * eviction happens and updated upon a file access.
   */
  private final Map<Long, Long> mFileIdToMaxMem = new HashMap<>();
  private final MasterInfo mMasterInfo;
  private Long mFileQueueLength = 0L, mBlockQueueLength = 0L;
  private final ConcurrentLinkedQueue<Pair<Integer, Long>> mAccessQueueFile =
      new ConcurrentLinkedQueue<>();
  private final ConcurrentLinkedQueue<Pair<Long, Long>> mAccessQueueBlock =
      new ConcurrentLinkedQueue<>();
  private final Map<Long, List<Long>> mWorkerIdToEvictionCandidate = new HashMap<>();

  public EvictGlobal(MasterInfo masterInfo) {
    mMasterInfo = masterInfo;
  }

  public synchronized Map<Long, List<WorkerBlockInfo>> getBlocksToEvict(NetAddress workerAddress,
      Set<Long> lockedBlocks, List<Long> candidateDirIds, long blockId, long requestBytes, boolean isLastTier) {
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
          if (sizeToEvict.get(workerBlockInfo.storageDirId) >= requestBytes) {
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
    //TODO: check dirs in candidateDir
    if (retDirId != null) {
      ret.put(retDirId, toEvictBlockInfos);
      return ret;
    }
    // use global info: pinlist, memory consumption of each file, allocated space of each file
    while (true) {

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
      synchronized (mFileQueueLength) {
        while (mFileQueueLength > QUEUE_SIZE) {
          Pair<Integer, Long> file = mAccessQueueFile.poll();
          if (file == null) {
            MasterInfo.getLog()
                .debug("EvictGlobal.updsateLRUCandidateFile: mAccessQueueFile is empty");
            return;
          }
          List<ClientBlockInfo> clientBlockInfos = mMasterInfo.getFileBlocks(file.getFirst());
          for (ClientBlockInfo blockInfo : clientBlockInfos) {
            for (NetAddress address : blockInfo.getLocations()) {
              long workerId = mMasterInfo.getmWorkerAddressToId().get(address);
              if (mWorkerIdToEvictionCandidate.containsKey(workerId) == false) {
                mWorkerIdToEvictionCandidate.put(workerId, new ArrayList<Long>());
              }
              mWorkerIdToEvictionCandidate.get(workerId).add(blockInfo.getBlockId());
            }
          }
          mFileQueueLength -= file.getSecond();
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
      synchronized (mBlockQueueLength) {
        while (mBlockQueueLength > QUEUE_SIZE) {
          Pair<Long, Long> block = mAccessQueueBlock.poll();
          if (block == null) {
            MasterInfo.getLog()
                .debug("EvictGlobal.updateLRUCandidateBlock: mAccessQueueBlock is empty");
            return;
          }
          ClientBlockInfo blockInfo = mMasterInfo.getClientBlockInfo(block.getFirst());
          for (NetAddress address : blockInfo.getLocations()) {
            long workerId = mMasterInfo.getmWorkerAddressToId().get(address);
            if (mWorkerIdToEvictionCandidate.containsKey(workerId) == false) {
              mWorkerIdToEvictionCandidate.put(workerId, new ArrayList<Long>());
            }
            mWorkerIdToEvictionCandidate.get(workerId).add(blockInfo.getBlockId());
          }
          mBlockQueueLength -= block.getSecond();
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
    mFileQueueLength += length;
  }

  public void accessBlock(long blockId) {
    try {
      long length = mMasterInfo.getClientBlockInfo(blockId).length;
      mAccessQueueBlock.add(new Pair<Long, Long>(blockId, length));
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
