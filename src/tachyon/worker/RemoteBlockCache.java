package tachyon.worker;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Closer;

import tachyon.Constants;
import tachyon.conf.UserConf;
import tachyon.thrift.BlockInfoException;
import tachyon.thrift.ClientBlockInfo;
import tachyon.thrift.FileAlreadyExistException;
import tachyon.thrift.FileDoesNotExistException;
import tachyon.thrift.InvalidPathException;
import tachyon.thrift.NetAddress;
import tachyon.thrift.OutOfSpaceException;
import tachyon.thrift.SuspectedFileSizeException;
import tachyon.thrift.TachyonException;
import tachyon.util.CommonUtils;
import tachyon.util.NetworkUtils;
import tachyon.worker.nio.DataServerMessage;

/**
 * learned from {@link tachyon.client.BlockOutStream} and {@link tachyon.client.RemoteBlockInStream}
 * 
 * @author guojunshi
 *
 */
public class RemoteBlockCache {
  /**
   * The number of bytes to read remotely every time we need to do a remote read
   */
  private static final int BUFFER_SIZE = UserConf.get().REMOTE_READ_BUFFER_SIZE_BYTE;
  private static final int MAX_REMOTE_READ_ATTEMPTS = 2;
  private static final Logger LOG = LoggerFactory.getLogger(Constants.LOGGER_TYPE);

  private final RandomAccessFile mLocalFile;
  private final FileChannel mLocalFileChannel;
  private boolean mPermission = false;
  private final String mFilePath;
  private final Closer mCloser = Closer.create();
  private ByteBuffer mCurrentBuffer = null;
  private ClientBlockInfo mBlockInfo = null;
  private long mBlockPos = 0;
  private long mBufferStartPos;
  private long mAvailableBytes = 0;

  private WorkerStorage mWorkerStorage;
  private long mUserId;

  public RemoteBlockCache(WorkerStorage ws, long userId, ClientBlockInfo blockInfo)
      throws OutOfSpaceException, FileAlreadyExistException, IOException {
    this.mAvailableBytes = BUFFER_SIZE;
    this.mWorkerStorage = ws;
    this.mBlockInfo = blockInfo;
    this.mBlockPos = 0;
    this.mBufferStartPos = 0;
    this.mUserId = userId;

    mFilePath = getLocalBlockTemporaryPath(mBlockInfo.blockId, BUFFER_SIZE);
    LOG.info("RemoteBlockCache: mFilePath=" + mFilePath);
    mLocalFile = mCloser.register(new RandomAccessFile(mFilePath, "rw"));
    LOG.info("RemoteBlockCache: mLocalFile=" + mLocalFile.toString());
    mLocalFileChannel = mCloser.register(mLocalFile.getChannel());
    LOG.info("RemoteBlockCache: mLocalFileChannel=" + mLocalFileChannel.toString());
    // change the permission of the temporary file in order that the worker
    // can move it.
    CommonUtils.changeLocalFileToFullPermission(mFilePath);
    // use the sticky bit, only the client and the worker can write to the
    // block
    CommonUtils.setLocalFileStickyBit(mFilePath);
    LOG.info(mFilePath + " was created!");
  }

  public void cache() throws TachyonException, FileDoesNotExistException, IOException {
    while (mBlockPos < mBlockInfo.length && updateCurrentBuffer()) {
      // cache whole mCurrentBlock
      byte[] b = new byte[mCurrentBuffer.remaining()];
      // mCurrentBuffer.flip();
      mCurrentBuffer.get(b);
      int bytesToRead = b.length;
      if (mAvailableBytes < bytesToRead) {
        if (mWorkerStorage.requestSpace(mUserId, mBlockInfo.blockId,
            bytesToRead - mAvailableBytes)) {
          mAvailableBytes = bytesToRead;
        }
      }
      if (mAvailableBytes >= bytesToRead) {
        MappedByteBuffer out = mLocalFileChannel.map(MapMode.READ_WRITE, mBlockPos, bytesToRead);
        out.put(b);
        mAvailableBytes -= bytesToRead;
        mBlockPos += b.length;
      } else {
        cancelCache();
        throw new IOException(String.format(
            "No enough space on local worker: mUserId(%d)" + " blockId(%d) requestSize(%d)",
            mUserId, mBlockInfo.blockId, bytesToRead));
      }
    }
    // TODO: cache
    try {
      mWorkerStorage.cacheBlock(mUserId, mBlockInfo.blockId);
    } catch (SuspectedFileSizeException e) {
      LOG.error("RemoteBlockCache: data transferred successfully! cache failed!");
      LOG.error(e.getMessage());
    } catch (BlockInfoException e) {
      LOG.error("RemoteBlockCache: data transferred successfully! cache failed!");
      LOG.error(e.getMessage());
    }
  }

  public void cache(long offset, long len) {
    // TODO:

  }

  public void cancelCache() {
    mWorkerStorage.cancelBlock(mUserId, mBlockInfo.blockId);
  }

  public boolean updateCurrentBuffer() {
    if (mCurrentBuffer != null && mBufferStartPos <= mBlockPos
        && mBlockPos < Math.min(mBufferStartPos + BUFFER_SIZE, mBlockInfo.length)) {
      mCurrentBuffer.position((int) (mBlockPos - mBufferStartPos));
      return true;
    }

    mBufferStartPos = mBlockPos;
    long length = Math.min(BUFFER_SIZE, mBlockInfo.length - mBufferStartPos);
    LOG.info("Try to find remote worker and read block {} from {}, with len {}", mBlockInfo.blockId,
        mBufferStartPos, length);

    for (int i = 0; i < MAX_REMOTE_READ_ATTEMPTS; i ++) {
      mCurrentBuffer = readRemoteByteBuffer(mBlockInfo, mBufferStartPos, length);
      if (mCurrentBuffer != null) {
        mCurrentBuffer.position(0);
        return true;
      }
      // The read failed, refresh the block info and try again
      mBlockInfo = mWorkerStorage.getClientBlockInfo(mBlockInfo.blockId);
    }
    return false;
  }

  public static ByteBuffer readRemoteByteBuffer(ClientBlockInfo blockInfo, long offset, long len) {
    ByteBuffer buf = null;

    try {
      List<NetAddress> blockLocations = blockInfo.getLocations();
      LOG.info("RemoteBlockCache: Block locations:" + blockLocations);

      for (NetAddress blockLocation : blockLocations) {
        String host = blockLocation.mHost;
        int port = blockLocation.mSecondaryPort;

        // The data is not in remote machine's memory if port == -1.
        if (port == -1) {
          continue;
        }
        if (host.equals(InetAddress.getLocalHost().getHostName())
            || host.equals(InetAddress.getLocalHost().getHostAddress())
            || host.equals(NetworkUtils.getLocalHostName())) {
          LOG.warn(
              "RemoteBlockCache: Master thinks the local machine has data, But not! blockId:{}",
              blockInfo.blockId);
        }
        LOG.info("RemoteBlockCache:" + host + ":" + port + " current host is "
            + NetworkUtils.getLocalHostName() + " " + NetworkUtils.getLocalIpAddress());

        try {
          buf = retrieveByteBufferFromRemoteMachine(new InetSocketAddress(host, port),
              blockInfo.blockId, offset, len);
          if (buf != null) {
            break;
          }
        } catch (IOException e) {
          LOG.error("RemoteBlockCache: Fail to retrieve byte buffer for block " + blockInfo.blockId
              + " from remote " + host + ":" + port + " with offset " + offset + " and length "
              + len, e);
          buf = null;
        }
      }
    } catch (IOException e) {
      LOG.error("RemoteBlockCache: Failed to get read data from remote ", e);
      buf = null;
    }

    return buf;
  }

  /**
   * exact the same as retriveByteBufferFromRemoteMachine in RemoteBlockInStream
   * 
   * @param address
   * @param blockId
   * @param offset
   * @param length
   * @return
   * @throws IOException
   */
  private static ByteBuffer retrieveByteBufferFromRemoteMachine(InetSocketAddress address,
      long blockId, long offset, long length) throws IOException {
    SocketChannel socketChannel = SocketChannel.open();
    try {
      socketChannel.connect(address);

      LOG.info("RemoteBlockCache: Connected to remote machine " + address + " sent");
      DataServerMessage sendMsg =
          DataServerMessage.createBlockRequestMessage(blockId, offset, length);
      while (!sendMsg.finishSending()) {
        sendMsg.send(socketChannel);
      }

      LOG.info("RemoteBlockCache: Data " + blockId + " to remote machine " + address + " sent");

      DataServerMessage recvMsg =
          DataServerMessage.createBlockResponseMessage(false, blockId, null);
      while (!recvMsg.isMessageReady()) {
        int numRead = recvMsg.recv(socketChannel);
        if (numRead == -1) {
          LOG.warn("RemoteBlockCache: Read nothing");
        }
      }
      LOG.info(
          "RemoteBlockCache: Data " + blockId + " from remote machine " + address + " received");

      if (!recvMsg.isMessageReady()) {
        LOG.info("RemoteBlockCache: Data " + blockId + " from remote machine is not ready.");
        return null;
      }

      if (recvMsg.getBlockId() < 0) {
        LOG.info("RemoteBlockCache: Data " + recvMsg.getBlockId() + " is not in remote machine.");
        return null;
      }
      return recvMsg.getReadOnlyData();
    } finally {
      socketChannel.close();
    }
  }

  private void checkPermission() throws IOException {
    if (!mPermission) {
      // change the permission of the file and use the sticky bit
      CommonUtils.changeLocalFileToFullPermission(mFilePath);
      CommonUtils.setLocalFileStickyBit(mFilePath);
      mPermission = true;
    }
  }

  private String getLocalBlockTemporaryPath(long blockId, long initialBytes)
      throws IOException, OutOfSpaceException, FileAlreadyExistException {
    String blockPath = mWorkerStorage.requestBlockLocation(mUserId, blockId, initialBytes);

    File localTempFolder;
    try {
      localTempFolder = new File(CommonUtils.getParent(blockPath));
    } catch (InvalidPathException e) {
      throw new IOException(e);
    }

    if (!localTempFolder.exists()) {
      if (localTempFolder.mkdirs()) {
        CommonUtils.changeLocalFileToFullPermission(localTempFolder.getAbsolutePath());
        LOG.info("Folder {} was created!", localTempFolder);
      } else {
        throw new IOException("Failed to create folder " + localTempFolder);
      }
    }

    return blockPath;
  }

}
