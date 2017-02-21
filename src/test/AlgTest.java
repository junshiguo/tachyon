package test;

import java.util.Map;

import tachyon.Constants;
import tachyon.master.EvictGlobalInfo;

public class AlgTest {

  public static void main(String[] args) {
    EvictGlobalInfo info = new EvictGlobalInfo(null);
    info.accessFileTest(4, 40);
    info.accessFileTest(2, 20);
    info.accessFileTest(3, 30);
    info.accessFileTest(5, 50);
    info.accessFileTest(3, 30);
    info.accessFileTest(4, 40);
    Map<Integer, Long> plan = info.getMemAllocationPlanTest();
  }

}
