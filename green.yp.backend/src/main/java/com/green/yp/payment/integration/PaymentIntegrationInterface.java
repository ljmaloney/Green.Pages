package com.green.yp.payment.integration;

public interface PaymentIntegrationInterface {
  PaymentIntegrationResponse applyPayment(
      PaymentIntegrationRequest paymentRequest, String requestIP);
}
