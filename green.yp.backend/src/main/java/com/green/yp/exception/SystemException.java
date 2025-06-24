package com.green.yp.exception;

import org.springframework.http.HttpStatus;

public class SystemException extends BusinessException {
  public SystemException(String message, HttpStatus httpStatus, ErrorCodeType errorCode){
    super(message, HttpStatus.INTERNAL_SERVER_ERROR, errorCode);
  }
  public SystemException(String message, Throwable th) {
    super(message, th);
  }
}
