package demanadthree.ifrefactor;

/**
 * if 语句块在循坏体重
 */
public class ForGuardStmTReFactor {

  /**
   * 去 else
   */
  public double whileTest(int index) {
    double result = 0.0;
    while (index < 100) {
      if (_isDead()) {
        result = deAdAmOut();
        continue;
      }
      if (_isSeparated()) {
        result = separatedAmount();
        continue;
      }
      if (_isRetired()) {
        result = retiredAmount();
        continue;
      }
      return normalPayAmount();
    }
    return result;
  }

  /**
   * 返回值 表达式取反取反
   */
  public double test1(double _capital, double _initRate, double _duration, double _income,
      double ADJ_FACTOR) {
    double result = 0.0;
    int index = 10;
    while (index < 100) {
      if (_capital <= 0.0) {
        continue;
      }
      if (_initRate >= 100 || _duration <= 0.0) {
        continue;
      }
      if (_initRate >= 0 || _duration <= 100) {
        continue;
      }
      result = (_income / _duration) * ADJ_FACTOR;
    }
    return result;
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

  private boolean isDrink() {
    return false;
  }

  private boolean isInPart() {
    return false;
  }

  private boolean isInCar() {
    return false;
  }

  private boolean isToChatWith() {
    return false;
  }

  private boolean isWork() {
    return false;
  }

  private boolean isSleep() {
    return false;
  }

  private boolean isInCompany() {
    return false;
  }

  private boolean isEatBreakfast() {
    return false;
  }

  private double normalPayAmount() {
    return 0;
  }

  private double retiredAmount() {
    return 0;
  }

  private boolean _isRetired() {
    return false;
  }

  private double separatedAmount() {
    return 0;
  }

  private boolean _isSeparated() {
    return false;
  }

  private double deAdAmOut() {
    return 0;
  }

  private boolean _isDead() {
    return false;
  }
}
