package com.green.yp.api.apitype.producer.enumeration;

import lombok.Getter;

@Getter
public enum LocationDisplayType {
  NO_DISPLAY("Do not display this location in the search results"), // for admin / home offices
  CITY_STATE_ZIP(
      "Display only the city, state, and zip code in search results"), // display approximate
  // location, does not display
  // full address
  FULL_ADDRESS("Display the full address in the search results"); // display all address information

  private final String displayLabel;

  LocationDisplayType(String displayLabel) {
    this.displayLabel = displayLabel;
  }
}
