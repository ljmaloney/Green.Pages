package com.green.yp.geolocation.service.impl;

import com.green.yp.geolocation.data.model.PostalCodeGeocode;
import com.green.yp.geolocation.data.repository.PostalCodeGeocodeRepository;
import com.green.yp.geolocation.service.GeocodeLocation;
import com.green.yp.geolocation.service.GeocodingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service("defaultGeocodeService")
@Slf4j
@ConditionalOnProperty(name = "greenyp.geocoder.impl", havingValue = "default", matchIfMissing = true)
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
            .orElseGet( () -> {
              log.warn("No Geocode (lat/ long) found for zip code {}", zipCode);
                return new GeocodeLocation(BigDecimal.valueOf(33.771759), BigDecimal.valueOf(-84.367154));
              });
  }

  @Cacheable("geocodeZip")
  @Override
  public GeocodeLocation getCoordinates(String address, String city, String state, String zip) {
    if (StringUtils.isNotBlank(zip)) {
      return getCoordinates(zip);
    }
    Optional<PostalCodeGeocode> geocodeOptional = repository.findFirstByPlaceNameAndState(city, state);
    return geocodeOptional.map(gc -> new GeocodeLocation(gc.getLatitude(), gc.getLongitude()))
            .orElseGet( () -> {
              log.warn("No Geocode (lat/ long) found for placeName {} and state {}", city, state);
              return new GeocodeLocation(BigDecimal.valueOf(0), BigDecimal.valueOf(0));
            });
  }
}
