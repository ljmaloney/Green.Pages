package com.green.yp.geolocation.service;

import org.springframework.stereotype.Service;

@Service
public interface GeocodingService {
  GeocodeLocation getCoordinates(String zipCode);
}
