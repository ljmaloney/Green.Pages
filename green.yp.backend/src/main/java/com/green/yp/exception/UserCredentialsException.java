package com.green.yp.exception;

import org.springframework.http.HttpStatus;

public class UserCredentialsException extends BusinessException {
  public UserCredentialsException(String message, Throwable th) {
    super(message, th);
  }

  public UserCredentialsException(String userName, String emailAddress) {
    super("User credentials exist for userName %s and email address %s",
            HttpStatus.PRECONDITION_FAILED,
            ErrorCodeType.BUSINESS_VALIDATION_ERROR, userName, emailAddress);
  }
}
