package com.green.yp.geolocation.service;

import com.green.yp.geolocation.data.model.PostalCodeGeocode;
import com.green.yp.geolocation.data.repository.PostalCodeGeocodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
public class DefaultGeocodeServiceImpl implements GeocodingService {

  private final PostalCodeGeocodeRepository repository;

  public DefaultGeocodeServiceImpl(PostalCodeGeocodeRepository repository){
    this.repository = repository;
  }

  @Override
  @Cacheable("geocodeZip")
  public GeocodeLocation getCoordinates(String zipCode) {
    Optional<PostalCodeGeocode> geocodeOptional = repository.findById(zipCode);
    return geocodeOptional.map(gc -> new GeocodeLocation(gc.getLatitude(), gc.getLongitude()))
            .orElse(new GeocodeLocation(BigDecimal.valueOf(33.771759), BigDecimal.valueOf(-84.367154)));
  }
}
