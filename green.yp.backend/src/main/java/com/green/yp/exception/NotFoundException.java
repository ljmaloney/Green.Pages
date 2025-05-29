package com.green.yp.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class NotFoundException extends BusinessException {

  public NotFoundException(String message) {
    super(message, HttpStatus.NOT_FOUND, ErrorCodeType.NOT_FOUND);
  }

  public NotFoundException(String resourceName, Object resourceIdentifier) {
    super(
        "Resource %s identified by [%s] was not found",
        HttpStatus.NOT_FOUND, ErrorCodeType.NOT_FOUND, resourceName, resourceIdentifier);
  }

  public NotFoundException(String messageFmt, Object... args) {
    super(messageFmt, HttpStatus.NOT_FOUND, ErrorCodeType.NOT_FOUND, args);
  }

  public NotFoundException(String resourceName, Map resourceKeyMap) {
    super(
        "Resource %s identified by [%s] was not found",
        HttpStatus.NOT_FOUND, ErrorCodeType.NOT_FOUND, resourceName, resourceKeyMap);
  }
}
