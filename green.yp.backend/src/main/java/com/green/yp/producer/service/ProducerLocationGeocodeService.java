package com.green.yp.producer.service;

import com.green.yp.geolocation.service.impl.DefaultGeocodeServiceImpl;
import com.green.yp.geolocation.service.GeocodingService;
import com.green.yp.geolocation.service.LiveGeocodeService;
import com.green.yp.producer.data.model.ProducerLocation;
import org.springframework.stereotype.Service;

@Service
public class ProducerLocationGeocodeService {

  private final GeocodingService defaultGeocodeService;
  private final LiveGeocodeService liveGecodeService;

    public ProducerLocationGeocodeService(DefaultGeocodeServiceImpl defaultGeocodeService,
                                          LiveGeocodeService liveGecodeService) {
        this.defaultGeocodeService = defaultGeocodeService;
        this.liveGecodeService = liveGecodeService;
    }

    public void geocodeLocation(ProducerLocation location) {
    // call geocode service
    // update repository
  }
}
