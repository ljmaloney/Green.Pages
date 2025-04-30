package com.green.yp.config;

public enum CacheEnumeration {
  ALL_LINE_OF_BUSINESS("allLineOfBusiness"),
  LINE_OF_BUSINESS("lineOfBusiness"),
  LINE_OF_BUSINESS_SERVICE("lineOfBusiness.service"),
  ACTIVE_SUBSCRIPTIONS("activeSubscriptions"),
  SUBSCRIPTION("subscription");

  public final String cacheName;

  CacheEnumeration(String cacheName) {
    this.cacheName = cacheName;
  }

  public String cacheName() {
    return cacheName;
  }
}
