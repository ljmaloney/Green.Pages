package com.green.yp.config;

import lombok.Getter;

@Getter
public enum CacheEnumeration {
  ALL_LINE_OF_BUSINESS("allLineOfBusiness", 60, 60, false, false),
  LINE_OF_BUSINESS("lineOfBusiness", 45, 60, false, false),
  LINE_OF_BUSINESS_SERVICE("lineOfBusiness.service", 30, 30, false, false),
  ACTIVE_SUBSCRIPTIONS("activeSubscriptions", 30, 30, false, false),
  SUBSCRIPTION("subscription", 30, 30, false, false),
  GEOCODE_ZIP("geocodeZip", 120, 120, false, true); // Longer duration, more permanent data

  public final String cacheName;
  public final long expirationMinutes;
  public final long idleScanTime;
  public final boolean permitNullValues;
  public final boolean eternal;

  CacheEnumeration(
      String cacheName,
      long defaultExpirationMinutes,
      long idleScanTime,
      boolean defaultPermitNullValues,
      boolean defaultEternal) {
    this.cacheName = cacheName;
    this.expirationMinutes = defaultExpirationMinutes;
    this.permitNullValues = defaultPermitNullValues;
    this.eternal = defaultEternal;
    this.idleScanTime = idleScanTime;
  }

  public String cacheName() {
    return cacheName;
  }

  public static CacheEnumeration fromCacheName(String cacheName) {
    for (CacheEnumeration cache : values()) {
      if (cache.cacheName.equals(cacheName)) {
        return cache;
      }
    }
    throw new IllegalArgumentException("No cache found with name: " + cacheName);
  }
}
