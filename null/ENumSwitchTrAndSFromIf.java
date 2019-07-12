package demanadthree.switchtrandfromif;

import demanadthree.iftandformsswitch.EnumClass;
import demanadthree.iftandformsswitch.ENumT;

/**
 * condition 为枚举类型
 */
public class ENumSwitchTrAndSFromIf {

  private EnumT.Colour colour;

  private EnumClass eNumClass;

  enum Season {

    SPRING, SUMMER, AUTUMN, WINTER
  }

  private Season spring = Season.SPRING;

  private void test() {
    if (colour == EnumT.Colour.RED) {
      System.out.println("red");
    } else if (colour == EnumT.Colour.BLUE) {
      System.out.println("blue");
    } else {
      System.out.println("none");
    }
  }

  private void test2() {
    if (eNumClass == EnumClass.IN) {
      System.out.println("IN");
    } else if (eNumClass == EnumClass.ERROR) {
      System.out.println("Error");
    }
  }

  private void test3() {
    if (spring == EnumSwitchTrandsFromIf.Season.AUTUMN) {
      System.out.println("秋天");
    } else if (spring == EnumSwitchTrandsFromIf.Season.SPRING) {
      System.out.println("春天");
    }
  }
}
