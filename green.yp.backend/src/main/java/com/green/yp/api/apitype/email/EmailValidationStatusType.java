package com.green.yp.api.apitype.email;

public enum EmailValidationStatusType {
  NOT_VALIDATED(false),
  VALIDATED(true),
  INVALID_BOUNCING(false);

  boolean valid;

  EmailValidationStatusType(boolean valid) {
    this.valid = valid;
  }

  public boolean isValidated() {
    return valid;
  }
}
