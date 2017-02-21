package tachyon.worker.eviction;

public class LFUUnit implements Comparable<LFUUnit> {

  private long mBlockId;
  private int mAccessCount;

  public LFUUnit(long blockId) {
    mBlockId = blockId;
    mAccessCount = 0;
  }

  public void addAccess() {
    mAccessCount ++;
  }

  @Override
  public int compareTo(LFUUnit o) {
    return mAccessCount - o.mAccessCount;
  }

}
