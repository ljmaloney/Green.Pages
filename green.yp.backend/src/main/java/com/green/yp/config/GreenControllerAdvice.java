package com.green.yp.config;

import com.green.yp.common.dto.ErrorMessageApi;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.exception.ErrorCodeType;
import com.green.yp.exception.NotFoundException;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.exception.UserCredentialsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Slf4j
public class GreenControllerAdvice {
  @ExceptionHandler(Throwable.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseApi<Void> handleError(Throwable e) {
    log.error("An unexpected error occurred - {}", e.getMessage(), e);
    return new ResponseApi<>(
        null,
        new ErrorMessageApi(
            ErrorCodeType.SYSTEM_ERROR, "Unexpected error has occurred", e.getMessage()));
  }

  @ExceptionHandler(UserCredentialsException.class)
  @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
  public ResponseApi<Void> handleError(UserCredentialsException e) {
    log.warn("User credentials error: {}", e.getMessage());
    return new ResponseApi<>(null,
            new ErrorMessageApi(
                    ErrorCodeType.PAYLOAD_VALIDATION,
                    "One or more validation rules on the request failed.",
                    e.getMessage()));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseApi<Void> handleValidationConstraint(ConstraintViolationException e) {
    log.warn("A validation constraint on a payload was violated - {}", e.getMessage(), e);
    return new ResponseApi<>(
        null,
        new ErrorMessageApi(
            ErrorCodeType.PAYLOAD_VALIDATION,
            "One or more validation rules on the request failed.",
            e.getMessage()));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public String handleMissing(NoResourceFoundException ex, HttpServletRequest request) {
    log.warn("Static resource not found: {} {}", request.getMethod(), request.getRequestURI());
    return "Requested resource was not found";
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
