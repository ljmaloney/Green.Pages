package com.green.yp.api.apitype.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseApi<T> {

  private T response = null;
  private ErrorMessageApi error = null;
}
