package demanadthree.iftandformsswitch;

/**
 * 简单的转化 当if多条分支表达式类似 表达式另一条件类型一致 表达式条件为 == equals() 两种类型 if重构为switch
 */
public class SampleIfTrAndFromSSwitch {

  /**
   * if 语句中只有一个条件
   */
  public void onceConditionCode(int code) {
    switch (code) {
      case 1:
        // 以打印语句模拟if中的语句 if code==1 run
        System.out.println(code);
        break;
      case 2:
        System.out.println(code);
        break;
      case 3:
        System.out.println(code);
        break;
      case 4:
        System.out.println(code);
        break;
      case 5:
        System.out.println(code);
        break;
      case 6:
        System.out.println(code);
        break;
      case 7:
        System.out.println(code);
        break;
      default:
        System.out.println(100);
    }
  }
}
