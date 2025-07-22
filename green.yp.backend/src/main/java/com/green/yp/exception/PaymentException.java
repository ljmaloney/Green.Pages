package com.green.yp.exception;

public class PaymentException extends BusinessException {
  public PaymentException(String message) {
    super(message);
  }
  public PaymentException(Throwable th) {
    super(ErrorCodeType.UNEXPECTED_PAYMENT_ERROR, th);
  }
}
