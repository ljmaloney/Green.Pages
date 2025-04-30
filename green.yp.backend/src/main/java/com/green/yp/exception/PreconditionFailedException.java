package com.green.yp.exception;

import org.springframework.http.HttpStatus;

public class PreconditionFailedException extends BusinessException {
  public PreconditionFailedException(String message) {
    super(message, HttpStatus.PRECONDITION_FAILED, ErrorCodeType.BUSINESS_VALIDATION_ERROR);
  }

  public PreconditionFailedException(String messageFmt, Object... params) {
    super(
        messageFmt,
        HttpStatus.PRECONDITION_FAILED,
        ErrorCodeType.BUSINESS_VALIDATION_ERROR,
        params);
  }
}
