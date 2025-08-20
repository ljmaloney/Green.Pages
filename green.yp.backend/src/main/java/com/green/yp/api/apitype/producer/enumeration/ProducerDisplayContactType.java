package com.green.yp.api.apitype.producer.enumeration;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@Getter
public enum ProducerDisplayContactType {
  NO_DISPLAY("Do not display contact in search results"), // do not display in search results
  FULL_NAME_PHONE_EMAIL(
      "Display all details in search results"), // show full name with email & phone
  GENERIC_NAME_PHONE_EMAIL(
      "Display only generic name, phone, and email in search results"), // show generic name with
  // email & phone
  PHONE_EMAIL_ONLY(
      "Display only the phone and email in the search results"); // show phone / email only

  private final String displayLabel;

  ProducerDisplayContactType(String displayLabel) {
    this.displayLabel = displayLabel;
  }

  public static List<ProducerDisplayContactType> getValuesAsList() {
    return Arrays.asList(ProducerDisplayContactType.values());
  }
}
