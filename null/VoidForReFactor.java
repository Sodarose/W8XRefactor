package demanadthree.fortransfromwhile;

public class VoidForReFactor {

  public void test() {
    T: while (true) {
      Q: for (int code = 0; code > 100; code++) {
        if (code == 50) {
          break T;
        }
      }
    }
  }
}
