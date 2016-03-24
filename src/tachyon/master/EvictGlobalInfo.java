package tachyon.master;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import tachyon.Pair;

public class EvictGlobalInfo {
  private static final double A = 1.0 / (Math.E - 1);
  private static final long QUEUE_SIZE = 100 * 1024 * 1024;
  private static final long MEMORY_SUM = 10 * 1024 * 1024;

  private final MasterInfo mMasterInfo;
  private boolean mFileAccessed = true;
  /**
   * A map from file ID's to max in-memory block numbers. This is managed by master, looked up when
   * eviction happens and updated upon a file access.
   */
  private Map<Integer, Long> mFileIdToMaxMem = new HashMap<>();
  /**
   * Map from file id to file size, in the up coming order
   */
  private final LinkedList<Pair<Integer, Long>> mFileAccessQueue = new LinkedList<>();
  private Long mFileQueueLength = 0L;
  private final Map<Integer, Integer> mFileAccessCount = new HashMap<>();

  public EvictGlobalInfo(MasterInfo masterInfo) {
    mMasterInfo = masterInfo;
  }

  /**
   * called from outside
   * 
   * @param fileId
   */
  public synchronized void accessFile(int fileId) {
    Inode inode = mMasterInfo.getmFileIdToInodes().get(fileId);
    if (inode == null || inode.isDirectory()) {
      MasterInfo.getLog().error("EvictGlobalInfo.accessFile: {} is not a file.", inode);
      return;
    }
    mFileAccessed = true;
    long length = ((InodeFile) inode).getLength();
    mFileAccessQueue.add(new Pair<Integer, Long>(fileId, length));
    mFileQueueLength += length;
    if (mFileAccessCount.containsKey(fileId)) {
      mFileAccessCount.put(fileId, mFileAccessCount.get(fileId) + 1);
    } else {
      mFileAccessCount.put(fileId, 1);
    }
  }

  /**
   * call by worker through MasterInfo
   * 
   * @return
   */
  public synchronized Map<Integer, Long> getMemAllocationPlan() {
    if (!mFileAccessed) {
      return new HashMap<>(mFileIdToMaxMem);
    }

    updateQueue();
    cleanAccessCount();

    Map<Integer, Long> fileSizes = new HashMap<>();
    long fileSizeSum = 0;
    int accessCountSum = 0;
    Map<Integer, Integer> fileAccessCount = new HashMap<>(mFileAccessCount);
    for (int fileId : fileAccessCount.keySet()) {
      Inode inode = mMasterInfo.getmFileIdToInodes().get(fileId);
      if (inode == null || inode.isDirectory()) {
        MasterInfo.getLog().error("EvictGlobalInfo.getMemAllocationPlan: {} is not a file.", inode);
        continue;
      }
      long length = ((InodeFile) inode).getLength();
      fileSizes.put(fileId, length);
      fileSizeSum += length;
      accessCountSum += fileAccessCount.get(fileId);
    }

    Map<Integer, Long> fileIdToMaxMem = new HashMap<>();
    long dsi;
    long allocation;
    int fi;
    int workerNumber = mMasterInfo.getWorkerCount();
    double percent;
    for (int fileId : fileAccessCount.keySet()) {
      dsi = fileSizes.get(fileId);
      fi = fileAccessCount.get(fileId);
      percent = 1.0 * fi * (MEMORY_SUM - A * fileSizeSum) / (dsi * accessCountSum) - A;
      allocation = (long) (percent * dsi);
      allocation = Math.max(0, Math.min(dsi, allocation));
      fileIdToMaxMem.put(fileId, allocation / workerNumber);
    }
    mFileIdToMaxMem = fileIdToMaxMem;
    mFileAccessed = false;
    return fileIdToMaxMem;
  }

  /**
   * update queue to ensure that file sizes sum no more than QUEUE_SIZE
   */
  private synchronized void updateQueue() {
    while (mFileQueueLength > QUEUE_SIZE) {
      Pair<Integer, Long> file = mFileAccessQueue.poll();
      if (file == null) {
        MasterInfo.getLog().debug("EvictGlobal.updsateLRUCandidateFile: mFileAccessQueue is empty");
        return;
      }
      mFileQueueLength -= file.getSecond();
      if (!mFileAccessCount.containsKey(file.getFirst())) {
        MasterInfo.getLog().debug("EvictGlobalInfo: file id not found in mFileAccessCount");
        continue;
      }
      if (mFileAccessCount.get(file.getFirst()) == 1) {
        mFileAccessCount.remove(file.getFirst());
      } else {
        mFileAccessCount.put(file.getFirst(), mFileAccessCount.get(file.getFirst()) - 1);
      }
    }
  }

  /**
   * File may be deleted during two calculation. Check before calculating memory allocation plan.
   */
  private synchronized void cleanAccessCount() {
    Iterator<Map.Entry<Integer, Integer>> iterator = mFileAccessCount.entrySet().iterator();
    while (iterator.hasNext()) {
      int fileId = iterator.next().getKey();
      Inode inode = mMasterInfo.getmFileIdToInodes().get(fileId);
      if (inode == null || inode.isDirectory()) {
        iterator.remove();
      }
    }
  }

}
