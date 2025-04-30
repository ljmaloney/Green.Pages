package com.green.yp.api.apitype.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorMessageApi {
  private ErrorCodeType errorCode;
  private String displayMessage;
  private String errorDetails;
}
