package com.green.yp.payment.integration;

import com.green.yp.payment.data.enumeration.AvsResponseCode;
import com.green.yp.payment.data.enumeration.CvvResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class DefaultPaymentService implements PaymentIntegrationInterface {
  @Override
  public PaymentIntegrationResponse applyPayment(
      PaymentIntegrationRequest paymentRequest, String requestIP) {
    return new PaymentIntegrationResponse(
        UUID.randomUUID(),
        null,
        AvsResponseCode.MATCHES.getResponseCode(),
        AvsResponseCode.MATCHES.getResponseCode(),
        CvvResponseCode.MATCHES.getResponseCode(),
        "Success",
        "");
  }
}
