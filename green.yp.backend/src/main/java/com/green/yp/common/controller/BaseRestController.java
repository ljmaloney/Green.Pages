package com.green.yp.common.controller;

import com.green.yp.common.dto.ErrorMessageApi;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.exception.ErrorCodeType;
import com.green.yp.exception.NotFoundException;
import com.green.yp.exception.PreconditionFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
public abstract class BaseRestController {

  @ExceptionHandler(Throwable.class)
  public ResponseApi<Void> handleError(Throwable e) {
    log.error("An unexpected error occurred - {}", e.getMessage(), e);
    return new ResponseApi<>(
        null,
        new ErrorMessageApi(
            ErrorCodeType.SYSTEM_ERROR, "Unexpected error has occurred", e.getMessage()));
  }

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseApi<Void> handleError(NotFoundException e) {
    log.error("Requested resource was not found, message - {}", e.getMessage());
    return new ResponseApi<>(
        null,
        new ErrorMessageApi(e.getErrorCode(), "Requested resource was not found", e.getMessage()));
  }

  @ExceptionHandler(PreconditionFailedException.class)
  @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
  public ResponseApi<Void> handleError(PreconditionFailedException e) {
    log.error("One or more business rule validations failed - {}", e.getMessage());
    return new ResponseApi<>(
        null,
        new ErrorMessageApi(
            e.getErrorCode(), "One or more business rule validations failed ", e.getMessage()));
  }
}
