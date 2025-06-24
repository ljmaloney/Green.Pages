package com.green.yp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
  private final ErrorCodeType errorCode;
  private final HttpStatus httpStatus;

  public BusinessException(String message) {
    super(message);
    errorCode = ErrorCodeType.SYSTEM_ERROR;
    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
  }

  public BusinessException(String message, Throwable th) {
    super(message, th);
    errorCode = ErrorCodeType.SYSTEM_ERROR;
    httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
  }

  public BusinessException(String messageFormat, Throwable th, Object... params) {
    super(String.format(messageFormat, params), th);
    this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    this.errorCode = ErrorCodeType.SYSTEM_ERROR;
  }

  public BusinessException(ErrorCodeType errorCode, Object... params) {
    this(errorCode.getMessageFormat(), HttpStatus.INTERNAL_SERVER_ERROR, errorCode, params);
  }

  public BusinessException(ErrorCodeType errorCode, Throwable th, Object... params) {
    super(String.format(errorCode.getMessageFormat(), params), th);
    this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    this.errorCode = errorCode;
  }

  public BusinessException(
          String message, HttpStatus httpStatus, ErrorCodeType errorCode) {
    super(message);
    this.httpStatus = httpStatus;
    this.errorCode = errorCode;
  }

  public BusinessException(
      String messageFmt, HttpStatus httpStatus, ErrorCodeType errorCode, Object... params) {
    super(String.format(messageFmt, params));
    this.httpStatus = httpStatus;
    this.errorCode = errorCode;
  }
}
