package test;

import tachyon.client.ReadType;

public class CommonTest {
  public static void main(String[] args) {
    String val = "TRY_CACHE";
    System.out.println(Enum.valueOf(ReadType.class, val));
  }

}
