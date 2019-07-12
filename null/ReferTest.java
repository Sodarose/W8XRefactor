package demanadtwo.variablenametest;

public class ReferTest {

  double handle = NameTest.loginHandle;

  public void refer() {
    NameTest nameTest = new NameTest();
    int flag = nameTest.loginControl;
  }
}
tModel implements TestModel {

  // 类名引用中的成员普通变量依赖重构
  public TestModel model = new TestModel();

  // 类名引用中的成员List变量依赖重构
  public List<TestModel> modelArrayList = new ArrayList<TestModel>();

  // 类名引用中的成员Map变量依赖重构
  public Map<String, TestModel> modelMap = new HashMap<String, testModel>();

  // 类名引用中的成员Set变量依赖重构
  public Set<TestModel> modelSet = new HashSet<TestModel>();

  // 类名引用中的参数依赖重构
  public void test(TestModel model) {
    List<TestModel> modelArrayList1 = new ArrayList<TestModel>();
    Map<String, TestModel> modelMap1 = new HashMap<String, testModel>();
    Set<TestModel> modelSet1 = new HashSet<TestModel>();
    TestModel test = new TestModel();
  }
}
