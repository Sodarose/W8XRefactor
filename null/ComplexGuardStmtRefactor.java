package demanadthree.ifrefactor;

/**
 * 条件取反和去else 两种方式复合
 */
public class ComplexGuardStmTReFactor {

  private void test() {
    if (isInHome()) {
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
      return;
    }
    if (isInCompany()) {
      System.out.println("在公司");
      if (isWork()) {
        System.out.println("在工作");
        return;
      }
      if (isToChatWith()) {
        System.out.println("在聊天");
        return;
      }
      System.out.println("开小差");
      return;
    }
    System.out.println("不在家");
  }

  /**
   * 复杂去else
   */
  /**
   * 复杂条件取反
   */
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

  private boolean isDrink() {
    return false;
  }

  private boolean isInCar() {
    return false;
  }

  private boolean isInPart() {
    return false;
  }

  private boolean isToChatWith() {
    return false;
  }

  private boolean isWork() {
    return false;
  }

  private boolean isInCompany() {
    return false;
  }

  private boolean isInHome() {
    return false;
  }
}
