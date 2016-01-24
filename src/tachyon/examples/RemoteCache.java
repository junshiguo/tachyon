package tachyon.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.hadoop.hdfs.server.namenode.dfshealth_jsp;
import org.apache.thrift.TException;

import com.google.common.io.Closer;

import tachyon.TachyonURI;
import tachyon.client.OutStream;
import tachyon.client.TachyonFS;
import tachyon.client.TachyonFile;
import tachyon.client.WriteType;
import tachyon.thrift.ClientBlockInfo;
import tachyon.thrift.ClientWorkerInfo;
import tachyon.thrift.MasterService.AsyncProcessor.liststatus;
import tachyon.thrift.NetAddress;
import tachyon.util.ThreadFactoryUtils;
import tachyon.worker.WorkerClientRemote;

public class RemoteCache {
  ExecutorService mExecutorService =
      Executors.newFixedThreadPool(3, ThreadFactoryUtils.daemon("client-heartbeat-%d"));

  public static void main(String[] args) {

    try {
      RemoteCache remoteCache = new RemoteCache();
      remoteCache.doCache("/readme");
    } catch (IOException | TException e) {
      e.printStackTrace();
    }
  }

  public void doCache(String filepath) throws IOException, TException {
    // String masterHost = "10.141.211.85";
    // int masterPort = 19998;
    TachyonFS tachyonFS = TachyonFS.get(new TachyonURI("tachyon://10.141.211.85:19998"));
    List<ClientBlockInfo> blocks =
        tachyonFS.getFileBlocks(tachyonFS.getFileId(new TachyonURI(filepath)));
        //
        // mExecutorService =
        // Executors.newFixedThreadPool(3, ThreadFactoryUtils.daemon("client-heartbeat-%d"));
        // mMasterClient = closer.register(new MasterClient(new InetSocketAddress(masterHost,
        // masterPort), mExecutorService));
        // int fileId = mMasterClient.user_createFile(path, "",
        // UserConf.get().DEFAULT_BLOCK_SIZE_BYTE, true);
        // java.util.List<ClientBlockInfo> blocks = mMasterClient.user_getFileBlocks(fileId, path);
        /**
         * the block to be cached remote
         */
    ClientBlockInfo blockInfo = blocks.get(0);

    List<NetAddress> locations = blockInfo.getLocations();
    List<ClientWorkerInfo> workers = tachyonFS.getmMasterClient().getWorkersInfo();
    for (ClientWorkerInfo workerInfo : workers) {
      boolean exist = false;
      for (NetAddress address : locations) {
        if (workerInfo.getAddress().equals(address)
            || workerInfo.getAddress().mHost.equals(address.mHost)) {
          exist = true;
          break;
        }
      }
      if (exist == false) {
        System.err.println("Cache to remote worker: " + workerInfo.getAddress().toString());
        Closer mCloser = Closer.create();
        WorkerClientRemote worker =
            mCloser.register(new WorkerClientRemote(tachyonFS.getmMasterClient(),
                workerInfo.getAddress(), mExecutorService));
        worker.mustConnect();
        List<ClientBlockInfo> blockInfos = new ArrayList<>();
        blockInfos.add(blockInfo);
        worker.master_cacheFromRemote(-1, blockInfos);
        mCloser.close();
        System.err.println("Cache finished!");
      }
    }
  }

}
