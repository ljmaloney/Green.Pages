package com.green.yp.api.apitype.common.enumeration;

public enum DayOfWeekType {
  SUNDAY("Sunday"),
  MONDAY("Monday"),
  TUESDAY("Tuesday"),
  WEDNESDAY("Wednesday"),
  THURSDAY("Thursday"),
  FRIDAY("Friday"),
  SATURDAY("Saturday");
  private final String name;

  DayOfWeekType(String name) {
    this.name = name;
  }
  public String getName(){
    return this.name;
  }
}
