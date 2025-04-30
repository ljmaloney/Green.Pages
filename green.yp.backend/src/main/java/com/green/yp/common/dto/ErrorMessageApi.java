package com.green.yp.common.dto;

import com.green.yp.exception.ErrorCodeType;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorMessageApi {
  private ErrorCodeType errorCode;
  private String displayMessage;
  private String errorDetails;
}
