package com.green.yp.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthServiceResponse<T> {
  private String authServiceRefId;
  private T actualResponse;
}
