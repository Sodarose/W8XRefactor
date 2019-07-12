package demandfour;

/**
 * 缺少equals 就会在hashCode方法体上增加提示性注释
 */
public class HashCodeRule {

  /* 该类重写了hashCode函数 但是没有重写equals函数 */
  @Override
  public int hashCode() {
    return super.hashCode();
  }
}

/**
 * 缺少hashCode 就会在equals方法体上增加提示性注释
 */
class EqualsRule {

  /* 该类重写了equals函数 但是没有重写hashCode函数 */
  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }
}
