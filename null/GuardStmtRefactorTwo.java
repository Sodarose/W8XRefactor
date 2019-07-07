package demanadthree.ifrefactor;

/**
 * 卫语句重构测试用例二 条件取反
 */
public class GuardStmTReFactorTwo {

  /**
   * 带返回值
   */
  double getAdjustedCaptItAl(double _capital, double _initRate, double _duration, double _income,
      double ADJ_FACTOR) {
    double result = 0.0;
    if (_capital <= 0.0) {
      return result;
    }
    if (_initRate >= 100 || _duration <= 0.0) {
      return result;
    }
    if (_initRate >= 0 || _duration <= 100) {
      return result;
    }
    return (_income / _duration) * ADJ_FACTOR;
  }

  /**
   * 无返回值
   */
  private void test1() {
    if (!isInHome()) {
      return;
    }
    System.out.println("在家");
    if (!isEatLunch()) {
      return;
    }
    System.out.println("吃午餐");
    if (!issRice()) {
      return;
    }
    System.out.println("吃米饭");
    if (!isChopsticks()) {
      return;
    }
    System.out.println("用的筷子");
    if (!isEnd()) {
      return;
    }
    System.out.println("吃完了");
  }

  private boolean isEnd() {
    return false;
  }

  private boolean isChopsticks() {
    return false;
  }

  private boolean isSRice() {
    return false;
  }

  private boolean isEatLunch() {
    return false;
  }

  private boolean isInHome() {
    return false;
  }
}
