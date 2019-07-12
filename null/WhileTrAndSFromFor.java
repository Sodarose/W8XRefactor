package demanadthree.whiletrandsfromfor;

import java.io.File;

/**
 * while转换为for
 */
public class WhileTrAndSFromFor {

  private void test1() {
    int c = 0;
    File file = new File("xxx");
    for (int i = 0, b = 0; i < 100 && b < 100; i++, b++) {
      run();
    }
  }

  private void test2() {
    int i = 0;
    i++;
    for (int b = 0; i < 100 && b < 100; b++) {
      run();
      i++;
    }
  }

  private void test3() {
    T: for (int i = 0; i < 100; i++) {
      run();
    }
  }

  private void run() {}
}
