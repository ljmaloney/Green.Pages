package com.green.yp.reference.data.enumeration;

import java.util.Arrays;

public enum SubscriptionType {
  TOP_LEVEL(true), // top level subscriptions for all producer
  LINE_OF_BUSINESS(true), // subscriptions for specific lines of business
  ADD_ON(false), // addional services
  LINE_OF_BUSINESS_ADD_ON(false); // add ons for line of business

  Boolean primarySubscription;

  SubscriptionType(Boolean primary) {
    this.primarySubscription = primary;
  }

  public static SubscriptionType[] getPrimaries() {
    return Arrays.stream(values())
        .filter(t -> t.isPrimarySubscription())
        .toList()
        .toArray(new SubscriptionType[0]);
  }

  public boolean isPrimarySubscription() {
    return primarySubscription;
  }
}
