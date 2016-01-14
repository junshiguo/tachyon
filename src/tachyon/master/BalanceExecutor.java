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

package tachyon.master;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.io.Closer;

import tachyon.util.ThreadFactoryUtils;
import tachyon.worker.WorkerClient;

public class BalanceExecutor implements Runnable {
  private MasterClient mMasterClient;
  // private WorkerClient mWorkerClient;
  private final ExecutorService mExecutorService;
  private final Closer mCloser = Closer.create();
  private final InetSocketAddress mMasterAddress;
  private final boolean mZookeeperMode;

  public BalanceExecutor(InetSocketAddress masterAddress, boolean zookeeperMode)
      throws IOException {
    mMasterAddress = masterAddress;
    mZookeeperMode = zookeeperMode;
    mExecutorService =
        Executors.newFixedThreadPool(2, ThreadFactoryUtils.daemon("balance-executor-%d"));

    mMasterClient =
        mCloser.register(new MasterClient(mMasterAddress, mZookeeperMode, mExecutorService));
    // mWorkerClient = mCloser.register(new WorkerClient(mMasterClient, mExecutorService));
  }

  @Override
  public void run() {
    Thread.currentThread().setName("memory-balance-executor");
    while (!Thread.currentThread().isInterrupted()) {
      // TODO:
    }
  }

}
