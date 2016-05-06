package tachyon.client;

import tachyon.thrift.UserBlockAccessInfo;

public class BlockAccessInfo {
  public static final int READ_MEMORY = 1;
  public static final int READ_REMOTE = 2;
  public static final int READ_UFS = 3;

  private int mFileId;
  private long mBlockId;
  private long mSizeByte;
  private long mOpenTime;
  private long mCloseTime;
  private long mDuration;
  private int mReadSource;

  private boolean mClosed = false;

  public BlockAccessInfo(int fileId, long blockId, long blockSize, long timestamp, int source) {
    mFileId = fileId;
    mBlockId = blockId;
    mSizeByte = blockSize;
    mOpenTime = timestamp;
    this.mReadSource = source;
  }

  public synchronized void setReadSource(int source) {
    mReadSource = source;
  }

  public synchronized void setClose() {
    mCloseTime = System.currentTimeMillis();
    mDuration = mCloseTime - mOpenTime;
    mClosed = true;
  }

  public synchronized boolean isClosed() {
    return mClosed;
  }

  public synchronized UserBlockAccessInfo toUserBlockAccessInfo() {
    UserBlockAccessInfo ret = new UserBlockAccessInfo();
    ret.fileId = mFileId;
    ret.blockId = mBlockId;
    ret.sizeByte = mSizeByte;
    ret.openTimeMs = mOpenTime;
    ret.closeTimeMs = mCloseTime;
    ret.duration = mDuration;
    ret.readSource = mReadSource;
    return ret;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(mBlockId).append('\t');
    sb.append(mSizeByte).append('\t');
    sb.append(mOpenTime).append('\t');
    sb.append(mCloseTime).append('\t');
    sb.append(mReadSource);
    return sb.toString();
  }

}
