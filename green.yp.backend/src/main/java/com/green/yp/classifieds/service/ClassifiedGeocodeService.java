package com.green.yp.classifieds.service;

import com.green.yp.api.apitype.common.GeocodeLocation;
import com.green.yp.classifieds.data.model.Classified;
import com.green.yp.geolocation.service.GeocodingService;
import com.green.yp.geolocation.service.LiveGeocodeService;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Tag(
    name = "ClassifiedGeocodeService",
    description =
        "Geocoding service supporting rate-limiting and fallback when rate limit exceeded")
public class ClassifiedGeocodeService {

  private final LiveGeocodeService liveGecodeService;
  private final GeocodingService defaultGeocodeService;
  private final RateLimiter rateLimiter;
  private final CircuitBreaker circuitBreaker;

  public ClassifiedGeocodeService(
      Optional<LiveGeocodeService> liveService,
      @Qualifier("defaultGeocodeServiceImpl") GeocodingService fallbackService,
      @Qualifier("geocodeApiRateLimiter") RateLimiter rateLimiter,
      @Qualifier("geocodeApiCircuitBreaker") CircuitBreaker circuitBreaker) {
    this.liveGecodeService = liveService.orElse(null); // only available if one is enabled
    this.defaultGeocodeService = fallbackService;
    this.rateLimiter = rateLimiter;
    this.circuitBreaker = circuitBreaker;
  }

  public GeocodeLocation geocodeLocation(Classified classified, String streetAddress) {
    log.info("Geocoding location for classified ad {}", classified.getId());
    if (liveGecodeService == null) {
      return defaultGeocodeService.getCoordinates(
          streetAddress, classified.getCity(), classified.getState(), classified.getPostalCode());
    }

    Supplier<GeocodeLocation> decorated =
        CircuitBreaker.decorateSupplier(
            circuitBreaker,
            RateLimiter.decorateSupplier(
                rateLimiter,
                () ->
                    liveGecodeService.getCoordinates(
                        streetAddress,
                        classified.getCity(),
                        classified.getState(),
                        classified.getPostalCode())));
    try {
      return decorated.get();
    } catch (CallNotPermittedException cnpe) {
      log.warn(
          "CircuitBreaker or Rate Limimter tripped, falling back to postal code mapping from db : {}",
          cnpe.toString());
      return defaultGeocodeService.getCoordinates(
          streetAddress, classified.getCity(), classified.getState(), classified.getPostalCode());
    } catch (Exception e) {
      log.error(
          "Unexpected exception {}, falling back to postal code mapping from db ", e.toString());
      return defaultGeocodeService.getCoordinates(
          streetAddress, classified.getCity(), classified.getState(), classified.getPostalCode());
    }
  }
}
