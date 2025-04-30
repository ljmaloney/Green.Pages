package com.green.yp.api.apitype.enumeration;

import lombok.Getter;

@Getter
public enum EmailTemplateName {
  WELCOME_EMAIL("green.yp.account.welcome"),
  EMAIL_CONFIRMATION("green.yp.confirm.email"),
  ACCOUNT_CANCELLATION("green.yp.account.cancel");

  private final String templateName;

  EmailTemplateName(String templateName) {
    this.templateName = templateName;
  }
}
