package com.green.yp.api.apitype.producer.enumeration;

import lombok.Getter;

@Getter
public enum InvoiceCycleType {
  MONTHLY("Recurring Monthly", 1, true),
  QUARTERLY("Recurring Quarterly", 3, true),
  ANNUAL("Recurring Annual", 12, true),
  NONRECURRING_ANNUAL("Annual", 12, false);

  String cycleDescription;
  Integer months;
  Boolean autobill = Boolean.FALSE;

  InvoiceCycleType(String cycleDescription, Integer months, Boolean autobill) {
    this.cycleDescription = cycleDescription;
    this.months = months;
    this.autobill = autobill;
  }
}
