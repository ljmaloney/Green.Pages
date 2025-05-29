package com.green.yp.exception;

public class SystemException extends BusinessException {
  public SystemException(String message, Throwable th) {
    super(message, th);
  }
}
