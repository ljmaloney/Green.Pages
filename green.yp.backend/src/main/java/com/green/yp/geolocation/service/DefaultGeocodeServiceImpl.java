package com.green.yp.geolocation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class DefaultGeocodeServiceImpl implements GeocodingService {
  @Override
  public GeocodeLocation getCoordinates(String zipCode) {
    return new GeocodeLocation(BigDecimal.valueOf(33.771759), BigDecimal.valueOf(-84.367154));
  }
}
