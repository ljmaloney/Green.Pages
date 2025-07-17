package com.green.yp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCodeType {
  PAYLOAD_VALIDATION("One or more validations failed when processing the payload", HttpStatus.BAD_REQUEST.value()),
  BUSINESS_VALIDATION_ERROR(
      "Business rule validation failed", HttpStatus.PRECONDITION_FAILED.value()),
  LINE_OF_BUSINESS_EXISTS(
      "The line of business %s already exists", HttpStatus.PRECONDITION_FAILED.value()),
  NOT_FOUND("The requested resource was not found", HttpStatus.NOT_FOUND.value()),
  PRODUCER_NOT_FOUND("The requested producer %s was not found", HttpStatus.NOT_FOUND.value()),
  SYSTEM_ERROR("Unexpected internal error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  UNEXPECTED_PAYMENT_ERROR("Unexpected payment error", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  MAX_LOGIN_ATTEMPTS(
      "Max login attempts exceeded, contact account is locked for {} minutes",
      HttpStatus.LOCKED.value()),
  PAYMENT_CUSTOMER_ERROR("Error creating customer for payment method", HttpStatus.INTERNAL_SERVER_ERROR.value() );
  private final int errorCode;
  private final String messageFormat;

  ErrorCodeType(String messageFmt, int errorCode) {
    this.errorCode = errorCode;
    this.messageFormat = messageFmt;
  }
}
