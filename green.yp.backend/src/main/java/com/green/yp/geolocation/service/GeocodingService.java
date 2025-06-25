package com.green.yp.geolocation.service;

import com.green.yp.api.apitype.common.GeocodeLocation;
import org.springframework.stereotype.Service;

public interface GeocodingService {
  GeocodeLocation getCoordinates(String zipCode);

  GeocodeLocation getCoordinates(String address, String city, String state, String zip);
}
