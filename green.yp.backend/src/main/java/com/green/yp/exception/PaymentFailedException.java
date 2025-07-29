package com.green.yp.exception;

import org.springframework.http.HttpStatus;

public class PaymentFailedException extends BusinessException {
  public  PaymentFailedException(String paymentErrorStatus, String paymentErrorDetail) {
    super(
            "Payment failed with %s - %s", HttpStatus.PRECONDITION_FAILED, ErrorCodeType.PAYMENT_CUSTOMER_ERROR, paymentErrorStatus, paymentErrorDetail);
  }
}
