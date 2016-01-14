/*
 * Licensed to the University of California, Berkeley under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package tachyon.worker;

import java.io.IOException;
import java.net.InetSocketAddress;
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
      mHeartbeat =
          mExecutorService.submit(new HeartbeatThread(threadName, heartBeater,
              UserConf.get().HEARTBEAT_INTERVAL_MS));
      
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

  public synchronized boolean master_cacheFromRemote(long userId, ClientBlockInfo blockInfo)
      throws TException, IOException {
    mustConnect();

    try {
      return mClient.master_cacheFromRemote(userId, blockInfo);
    } catch (TException e) {
      throw e;
    }
  }

}
