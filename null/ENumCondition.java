package demanadthree.iftandformsswitch;

/**
 * 对 枚举变量重构
 */
public class ENumCondition {

  enum Season {

    SPRING, SUMMER, AUTUMN, WINTER
  }

  // 类变量
  Season spring = Season.SPRING;

  // 外部类变量
  EnumT.Colour red = EnumT.Colour.RED;

  // 枚举类
  EnumClass eNumClass = EnumClass.DELETE;

  /**
   * 内部
   */
  public void test1() {
    switch (spring) {
      case SPRING:
        System.out.println("Spring");
        break;
      case SUMMER:
        System.out.println("SUMMER");
        break;
      case AUTUMN:
        System.out.println("AUTUMN");
        break;
      case WINTER:
        System.out.println("WINTER");
        break;
      default:
        System.out.println("null");
    }
  }

  /**
   * 外部变量内
   */
  public void test2() {
    switch (red) {
      case RED:
        System.out.println("red");
        break;
      case BLUE:
        System.out.println("blue");
        break;
      case BLANK:
        System.out.println("blank");
        break;
      case YELLOW:
        System.out.println("yellow");
        break;
      case GREEN:
        System.out.println("green");
        break;
      default:
    }
  }

  /**
   * 枚举类
   */
  public void test3() {
    switch (eNumClass) {
      case DELETE:
        System.out.println("DELETE");
        break;
      case UPDATE:
        System.out.println("UPDATE");
        break;
      case ERROR:
        System.out.println("ERROR");
        break;
      case IN:
        System.out.println("IN");
        break;
      case OUT:
        System.out.println("OUT");
        break;
      case QUT:
        System.out.println("QUT");
        break;
      default:
    }
  }
}
