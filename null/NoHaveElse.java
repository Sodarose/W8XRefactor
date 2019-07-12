package demanadthree.iftandformsswitch;

/**
 * 有最终else 和 无最终else == 可以和 equals混用
 */
public class NoHaveElse {

  /**
   * 无最终else
   */
  public void test1(String code) {
    switch (code) {
      case "1":
        /**
         * 模拟if then语句块执行代码
         */
        System.out.println(code);
        break;
      case "2":
        System.out.println(code);
        break;
      case "3":
        System.out.println(code);
        break;
      case "4":
        System.out.println(code);
        break;
      case "5":
        System.out.println(code);
        break;
      default:
    }
  }

  /**
   * 有最终else
   */
  public void test2(String code) {
    switch (code) {
      case "1":
        /**
         * 模拟if then语句块执行代码
         */
        System.out.println(code);
        break;
      case "2":
        System.out.println(code);
        break;
      case "3":
        System.out.println(code);
        break;
      case "4":
        System.out.println(code);
        break;
      case "5":
        System.out.println(code);
        break;
      default:
        System.out.println("嘤嘤嘤");
    }
  }
}
