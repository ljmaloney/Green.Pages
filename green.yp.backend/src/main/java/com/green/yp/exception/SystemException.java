package com.green.yp.exception;

import org.springframework.http.HttpStatus;

public class SystemException extends BusinessException {
  public SystemException(String message, HttpStatus httpStatus, ErrorCodeType errorCode){
    super(message, httpStatus, errorCode);
  }
  public SystemException(String message, Throwable th) {
    super(message, th);
  }
}
