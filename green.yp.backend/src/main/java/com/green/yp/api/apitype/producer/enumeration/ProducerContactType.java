package com.green.yp.api.apitype.producer.enumeration;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;

public enum ProducerContactType {
  PRIMARY(
      "Primary contact information for display",
      true,
      true), // displayed when no separate SALES contact type
  ACCOUNTS_PAYABLE(
      "Accounts payable contact", false, false), // accounts payable not displayed on web page
  ADMIN(
      "Primary administrative contact for the business, never displayed in search results",
      false,
      true), // not displayed on web page
  DISABLED("Contact has been disabled", false, false),
  SALES("Sales contact information for display", true, false); // displayed on web page if exists

  private Boolean displayOnPage = Boolean.FALSE;
  private Boolean accountCreation = Boolean.FALSE;

  @Getter private final String displayLabel;

  ProducerContactType(String displayLabel, Boolean displayOnPage, Boolean accountCreation) {
    this.displayLabel = displayLabel;
    this.displayOnPage = displayOnPage;
    this.accountCreation = accountCreation;
  }

  public static List<ProducerContactType> getAccountCreationTypes() {
    return Arrays.stream(ProducerContactType.values())
        .filter(type -> type.isAccountCreation())
        .toList();
  }

  public static List<ProducerContactType> getActiveTypes() {
    return Arrays.stream(ProducerContactType.values())
        .filter(type -> type != ProducerContactType.DISABLED)
        .toList();
  }

  public Boolean isAccountCreation() {
    return accountCreation;
  }

  public Boolean isDisplayOnPage() {
    return displayOnPage;
  }
}
