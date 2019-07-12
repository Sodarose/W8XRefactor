package demanadthree.fortransfromwhile;

/**
 * for 转换为 while
 */
public class VoidForTransFromWhile {

  private void test1() {
    int code;
    while (true) {
      run();
    }
  }

  private void test2() {
    while (isRun()) {
      run();
    }
  }

  private void test3() {
    while (true) {
      run();
    }
  }

  private void test4() {
    int code = 0;
    while (true) {
      run();
      code++;
    }
  }

  private boolean isRun() {
    // xxxx
    return true;
  }

  private void run() {}
}
