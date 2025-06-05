package com.green.yp.api.enumeration;

public enum SubscriptionType {
  DATA_IMPORT_NO_DISPLAY(true),
  TOP_LEVEL(true), // top level subscriptions for all producer
  LINE_OF_BUSINESS(true), // subscriptions for specific lines of business
  ADD_ON(false), // addional services
  LINE_OF_BUSINESS_ADD_ON(false); // add ons for line of business

  final Boolean primarySubscription;

  SubscriptionType(Boolean primary) {
    this.primarySubscription = primary;
  }

  public boolean isPrimarySubscription() {
    return primarySubscription;
  }
}
