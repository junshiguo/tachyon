package tachyon.master;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import tachyon.Pair;
import tachyon.conf.MasterConf;

public class EvictGlobalInfo {
  // private static final double A = 1.0 / (Math.E - 1);
  private static long sQUEUESIZE;
  private static long sMEMORYSUM;

  private final MasterInfo mMasterInfo;
  private boolean mFileAccessed = true;
  /**
   * A map from file ID's to max in-memory block numbers. This is managed by master, looked up when
   * eviction happens and updated upon a file access.
   */
  private Map<Integer, Long> mFileIdToMaxMem = new HashMap<Integer, Long>();
  /**
   * Map from file id to file size, in the up coming order
   */
  private final LinkedList<Pair<Integer, Long>> mFileAccessQueue = new LinkedList<>();
  private Long mFileQueueLength = 0L;
  private final Map<Integer, Integer> mFileAccessCount = new HashMap<>();

  public EvictGlobalInfo(MasterInfo masterInfo) {
    mMasterInfo = masterInfo;
    // sQUEUESIZE = MasterConf.get().MEMORY_QUEUE_SIZE;
    sQUEUESIZE = MasterConf.get().MEMORY_QUEUE_SIZE;
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
    MasterInfo.getLog().info("******EvictGlobalInfo.accessFile: fileid = " + fileId + ", file = "
        + inode.getName() + ", file size = " + length + "******");
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

    MasterInfo.getLog().info(
        "***EvictGlobalInfo.getMemAllocationPlan: mFileAccessQueue:{}, mFileAccessCount:{}",
        mFileAccessQueue, mFileAccessCount);
    updateMemSize();
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
    // long dsi;
    long allocation;
    int fi;
    int workerNumber = mMasterInfo.getWorkerCount();
    StringBuilder sb = new StringBuilder("***EvictGlobalInfo.getMemAllocationPlan: memory sum="
        + sMEMORYSUM + ", access count sum=" + accessCountSum + ", file size sum=" + fileSizeSum
        + ", workerNumber=" + workerNumber + ", plan=[");
    double perAccess = 1.0 * sMEMORYSUM / accessCountSum;
    for (int fileId : fileAccessCount.keySet()) {
      // dsi = fileSizes.get(fileId);
      fi = fileAccessCount.get(fileId);
      allocation = (long) (fi * perAccess);
      // allocation = Math.min(dsi, allocation);
      fileIdToMaxMem.put(fileId, allocation / workerNumber);
      sb.append("[" + fileId + ", " + allocation / workerNumber + "],");
    }
    mFileIdToMaxMem = fileIdToMaxMem;
    mFileAccessed = false;
    sb.deleteCharAt(sb.length() - 1);
    sb.append("]***");
    MasterInfo.getLog().info(sb.toString());
    return fileIdToMaxMem;
  }

  public synchronized Map<Integer, Integer> getFileAccessCount() {
    return new HashMap<>(mFileAccessCount);
  }

  /**
   * update queue to ensure that file sizes sum no more than QUEUE_SIZE
   */
  private synchronized void updateQueue() {
    while (mFileQueueLength > sQUEUESIZE) {
      Pair<Integer, Long> file = mFileAccessQueue.poll();
      if (file == null) {
        MasterInfo.getLog().info("***EvictGlobal.updateQueue: mFileAccessQueue is empty");
        return;
      }
      mFileQueueLength -= file.getSecond();
      if (!mFileAccessCount.containsKey(file.getFirst())) {
        MasterInfo.getLog().info("***EvictGlobalInfo: file id not found in mFileAccessCount");
        continue;
      }
      if (mFileAccessCount.get(file.getFirst()) == 1) {
        mFileAccessCount.remove(file.getFirst());
      } else {
        mFileAccessCount.put(file.getFirst(), mFileAccessCount.get(file.getFirst()) - 1);
      }
      MasterInfo.getLog().info("***EvictGlobalInfo.updateQueue: remove file with id "
          + file.getFirst() + " from queue.***");
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
        MasterInfo.getLog().info(
            "***EvictGlobalInfo.cleanAccessCount: remove inode {} from mFileAccessCount.***",
            inode);
      }
    }
  }

  private void updateMemSize() {
    sMEMORYSUM = mMasterInfo.getCapacityBytes();
  }

  /**
   * for test only
   * 
   * @param fileId
   * @param length
   */
  public void accessFileTest(int fileId, long length) {
    mFileAccessQueue.add(new Pair<Integer, Long>(fileId, length));
    mFileQueueLength += length;
    if (mFileAccessCount.containsKey(fileId)) {
      mFileAccessCount.put(fileId, mFileAccessCount.get(fileId) + 1);
    } else {
      mFileAccessCount.put(fileId, 1);
    }
    System.out.println("***accessFile: fileid=" + fileId + ", length=" + length);
  }

  public Map<Integer, Long> getMemAllocationPlanTest() {
    updateMemSizeTest();
    updateQueue();

    Map<Integer, Long> fileSizes = new HashMap<>();
    long fileSizeSum = 0;
    int accessCountSum = 0;
    Map<Integer, Integer> fileAccessCount = new HashMap<>(mFileAccessCount);
    for (int fileId : fileAccessCount.keySet()) {
      long length = fileId * 10;
      fileSizes.put(fileId, length);
      fileSizeSum += length;
      accessCountSum += fileAccessCount.get(fileId);
    }

    Map<Integer, Long> fileIdToMaxMem = new HashMap<>();
    long dsi;
    long allocation;
    int fi;
    int workerNumber = 1;
    StringBuilder sb = new StringBuilder("***EvictGlobalInfo.getMemAllocationPlan: memory sum="
        + sMEMORYSUM + ", access count sum=" + accessCountSum + ", file size sum=" + fileSizeSum
        + ", workerNumber=" + workerNumber + ",plan:");
    double perAccess = 1.0 * sMEMORYSUM / accessCountSum;
    for (int fileId : fileAccessCount.keySet()) {
      dsi = fileSizes.get(fileId);
      fi = fileAccessCount.get(fileId);
      allocation = (long) (fi * perAccess);
      // allocation = Math.max(0, Math.min(dsi, allocation));
      fileIdToMaxMem.put(fileId, allocation / workerNumber);
      sb.append("[" + fileId + ", " + allocation / workerNumber + "],");
    }
    mFileIdToMaxMem = fileIdToMaxMem;
    mFileAccessed = false;
    sb.append("***");
    System.out.println(sb.toString());
    return fileIdToMaxMem;
  }

  private void updateMemSizeTest() {
    sMEMORYSUM = 10;
  }

}
