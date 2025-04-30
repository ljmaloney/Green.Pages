package com.green.yp.exception;

public class UserCredentialsException extends BusinessException {
  public UserCredentialsException(String message, Throwable th) {
    super(message, th);
  }
}
