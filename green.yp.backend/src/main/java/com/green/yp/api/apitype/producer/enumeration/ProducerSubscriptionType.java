package com.green.yp.api.apitype.producer.enumeration;

public enum ProducerSubscriptionType {
  ADMIN, // type used to create admin users
  BETA_TESTER, // identify beta_testers
  LIVE_ACTIVE, // active listing
  LIVE_CANCELED, // cancelled listing
  LIVE_DISABLED_NONPAYMENT, // disabled for non payment
  LIVE_UNPAID,
  DATA_LOAD_UNPAID // unpaid (new)
}
