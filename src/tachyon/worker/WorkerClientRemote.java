package tachyon.worker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tachyon.Constants;
import tachyon.HeartbeatExecutor;
import tachyon.HeartbeatThread;
import tachyon.conf.UserConf;
import tachyon.master.MasterClient;
import tachyon.thrift.ClientBlockInfo;
import tachyon.thrift.NetAddress;
import tachyon.thrift.WorkerService;
import tachyon.util.NetworkUtils;

/**
 * WorkerClientRemote is only used in cache from remote process. This class extends
 * {@link tachyon.worker.WorkerClient} and modifies connet() and add master_cacheFromRemote
 * 
 * @author guojunshi
 *
 */
public class WorkerClientRemote extends WorkerClient {
  private static final Logger LOG = LoggerFactory.getLogger(Constants.LOGGER_TYPE);

  public WorkerClientRemote(MasterClient masterClient, NetAddress workerAddress,
      ExecutorService executorService) throws IOException {
    super(masterClient, executorService);
    mWorkerAddress =
        new InetSocketAddress(NetworkUtils.getFqdnHost(workerAddress), workerAddress.mPort);
  }

  private synchronized boolean connect() throws IOException {
    if (!mConnected) {
      String host = mWorkerAddress.getHostString();
      int port = mWorkerAddress.getPort();
      mProtocol = new TBinaryProtocol(new TFramedTransport(new TSocket(host, port)));
      mClient = new WorkerService.Client(mProtocol);

      HeartbeatExecutor heartBeater =
          new WorkerClientHeartbeatExecutor(this, mMasterClient.getUserId());
      String threadName = "worker-heartbeat-" + mWorkerAddress;
      mHeartbeat = mExecutorService.submit(
          new HeartbeatThread(threadName, heartBeater, UserConf.get().HEARTBEAT_INTERVAL_MS));

      try {
        mProtocol.getTransport().open();
      } catch (TTransportException e) {
        LOG.error(e.getMessage(), e);
        return false;
      }
      mConnected = true;
    }

    return mConnected;
  }

  public synchronized void mustConnect() throws IOException {
    int tries = 0;
    while (tries ++ <= CONNECTION_RETRY_TIMES) {
      if (connect()) {
        return;
      }
    }
    throw new IOException("Failed to connect to the worker");
  }

  public synchronized boolean master_cacheFromRemote(long userId, List<ClientBlockInfo> blockInfos)
      throws TException, IOException {
    mustConnect();

    try {
      return mClient.master_cacheFromRemote(userId, blockInfos);
    } catch (TException e) {
      throw e;
    }
  }

}
