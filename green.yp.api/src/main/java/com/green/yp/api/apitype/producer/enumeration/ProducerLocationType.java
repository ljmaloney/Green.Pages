package com.green.yp.api.apitype.producer.enumeration;

import lombok.Getter;

@Getter
public enum ProducerLocationType {
  HOME_OFFICE_PRIMARY(
      "Primary business location"), // select for primary location, should always have one primary
  RETAIL_SALES_SERVICE(
      "Retail sales or service location"), // select for additional retail locations
  WHOLESALE_SALES("Wholesale sales and service location"); // select for wholesale only location
  private final String displayLabel;

  ProducerLocationType(String displayLabel) {
    this.displayLabel = displayLabel;
  }
}
