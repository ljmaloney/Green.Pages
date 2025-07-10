package com.green.yp.api.apitype.enumeration;

import lombok.Getter;

@Getter
public enum EmailTemplateType {
  WELCOME_EMAIL("green.yp.account.welcome","email/base-email.ftl", "Welcome to Green Market"),
  EMAIL_CONFIRMATION("green.yp.confirm.email", "email/base-email.ftl", "Please Confirm your email address"),
  ACCOUNT_CANCELLATION(
      "green.yp.account.cancel", "email/base-email.ftl", "Account Cancellation"),
  CLASSIFIED_CONFIRMATION("","email/classified-ad-confirmation.ftl", "Classified Ad Confirmation"),
  CLASSIFIED_AUTH_TOKEN("green.yp.classified.token", "email/classified-token.ftl", "Classified Authentication Token"),
  CLASSIFIED_EMAIL_VALIDATION("green.yp.classified.token","email/classified-token.ftl" , "GreenMarket - Classified Ad Email Validation");

  private final String templateName;
  private final String templateFileName;
  private final String subjectFormat;

  EmailTemplateType(String templateName, String templateFileName, String subjectFormat) {
    this.templateName = templateName;
    this.templateFileName = templateFileName;
    this.subjectFormat = subjectFormat;
  }
}
