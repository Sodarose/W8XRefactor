package demandfour;

import java.util.List;

/**
 * 不要在 foreach 循环里进行元素的 remove/add 操作
 */
public class ForEachRule {

  private List<String> list;

  public ForEachRule(List<String> list) {
    this.list = list;
  }

  public void test1() {
    for (String item : list) {
      run();
      // 不要在 foreach 循环里进行元素的 remove/add 操作
      list.remove("test1");
      test2(list);
    }
  }

  public void test2(List<String> list) {
    Test test = new Test();
    // 不要在 foreach 循环里进行元素的 remove/add 操作
    list.add("test2");
    test.test3(list);
  }

  public void run() {}
}

class Test {

  public void test3(List<String> list) {
    // 不要在 foreach 循环里进行元素的 remove/add 操作
    list.add("test3");
  }
}
