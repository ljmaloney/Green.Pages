package com.green.yp.exception.producer;

import com.green.yp.exception.BusinessException;
import com.green.yp.exception.ErrorCodeType;
import org.springframework.http.HttpStatus;

public class ContactAccountLockedException extends BusinessException {
  public ContactAccountLockedException(Integer timeLockMinutes) {
    super(
        "Max login attempts exceeded, contact account is locked for {} minutes",
        HttpStatus.LOCKED,
        ErrorCodeType.MAX_LOGIN_ATTEMPTS,
        timeLockMinutes);
  }
}
