package com.green.yp.producer.service;

import com.green.yp.producer.data.model.ProducerLocation;
import com.green.yp.producer.data.repository.ProducerLocationRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ProducerLocationGeocodeService {

  private final ProducerLocationRepository locationRepository;

  public ProducerLocationGeocodeService(ProducerLocationRepository repository) {
    this.locationRepository = repository;
  }

  @Async
  public void geocodeLocation(ProducerLocation location) {
    // call geocode service
    // update repository
  }
}
