package com.green.yp.geolocation.opencage.feign;

import com.green.yp.exception.ErrorCodeType;
import com.green.yp.exception.SystemException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

public class OpenCageErrorDecoder implements ErrorDecoder {

  private final ErrorDecoder defaultDecoder = new Default();

  @Override
  public Exception decode(String methodKey, Response response) {
    int status = response.status();

    if (status == 401 || status == 403) {
      return new SystemException(
          "Invalid OpenCage API key or access denied",
          HttpStatus.UNAUTHORIZED,
          ErrorCodeType.SYSTEM_ERROR);
    }

    if (status == 429) {
      return new SystemException(
          "Rate limit exceeded", HttpStatus.TOO_MANY_REQUESTS, ErrorCodeType.SYSTEM_ERROR);
    }

    if (status >= 500) {
      return new SystemException(
          "OpenCage server error: " + status,
          HttpStatus.INTERNAL_SERVER_ERROR,
          ErrorCodeType.SYSTEM_ERROR);
    }

    return defaultDecoder.decode(methodKey, response);
  }
}
