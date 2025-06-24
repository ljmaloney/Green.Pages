package com.green.yp.geolocation.service.impl;

import com.green.yp.api.apitype.common.GeocodeLocation;
import com.green.yp.exception.NotFoundException;
import com.green.yp.geolocation.data.model.PostalCodeGeocode;
import com.green.yp.geolocation.data.repository.PostalCodeGeocodeRepository;
import com.green.yp.geolocation.opencage.dto.OpenCageResponse;
import com.green.yp.geolocation.opencage.feign.OpenCageClient;
import com.green.yp.geolocation.service.LiveGeocodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service("openCageGeocodeService")
@Slf4j
@ConditionalOnProperty(name = "greenyp.geocoder.impl", havingValue = "opencage")
public class OpenCageGecodeServiceImpl implements LiveGeocodeService {

    @Value("${greenyp.geocode.url}")
    private String geocodeUrl;

    @Value ("${greenyp.geocode.apikey}")
    private String apiKey;

    private final OpenCageClient openCageClient;
    private final PostalCodeGeocodeRepository repository;

    public OpenCageGecodeServiceImpl(OpenCageClient client, PostalCodeGeocodeRepository repository){
        this.openCageClient = client;
        this.repository = repository;
    }

    @Override
    public GeocodeLocation getCoordinates(String zipCode) {
        return repository.findById(zipCode)
                .map( r -> new GeocodeLocation(r.getLatitude(), r.getLongitude()))
                .orElseThrow(() -> new NotFoundException("PostalCodeGeocode", zipCode));
    }

    @Override
    public GeocodeLocation getCoordinates(String address, String city, String state, String zip) {
        String addressQuery = String.join(" ", address, city, state, zip);
        OpenCageResponse response = openCageClient.geocode(addressQuery, apiKey, 1);
        log.debug("Geolocated address for {} to {}", address, response);
        return response.getResults()
                .stream()
                .findFirst()
                .map( result -> new GeocodeLocation(result.getGeometry().getLat(), result.getGeometry().getLng()))
                .orElseGet(() -> getCoordinates(zip));
    }
}
