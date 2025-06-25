package com.green.yp.geolocation.service.impl;

import com.green.yp.api.apitype.common.GeocodeLocation;
import com.green.yp.exception.NotFoundException;
import com.green.yp.geolocation.data.repository.PostalCodeGeocodeRepository;
import com.green.yp.geolocation.service.LiveGeocodeService;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.location.LocationClient;
import software.amazon.awssdk.services.location.model.SearchPlaceIndexForTextRequest;

@Service
@Slf4j
@ConditionalOnProperty(name = "greenyp.geocoder.impl", havingValue = "aws")
public class AwsGeocodeServiceImpl  implements LiveGeocodeService {
    @Value("${greenyp.geocoder.aws.placeIndex}")
    private String placeIndex;
    private final PostalCodeGeocodeRepository repository;
    private final LocationClient awsLocationClient;

    public AwsGeocodeServiceImpl(LocationClient awsLocationClient,
                                 PostalCodeGeocodeRepository repository){
        this.awsLocationClient = awsLocationClient;
        this.repository = repository;
    }

    @Override
    public GeocodeLocation getCoordinates(String zipCode) {
        return repository.findById(zipCode)
                .map(pc -> new GeocodeLocation(pc.getLatitude(), pc.getLongitude()))
                .orElseThrow( () -> new NotFoundException("PostalCodeGeocode", zipCode));
    }

    @Override
    public GeocodeLocation getCoordinates(String address, String city, String state, String zip) {
        String fullAddress = String.join(", ", address, city, state, zip);
        log.debug("Resolving lat/long for {}", fullAddress);
        try {
            var request = SearchPlaceIndexForTextRequest.builder()
                    .indexName(placeIndex)
                    .text(fullAddress)
                    .maxResults(1)
                    .build();

            var response = awsLocationClient.searchPlaceIndexForText(request);
            if (!response.results().isEmpty()) {
                var position = response.results().getFirst().place().geometry().point();
                log.debug("Found geocde for {} as {}", fullAddress, position);
                return new GeocodeLocation(BigDecimal.valueOf(position.get(1)), BigDecimal.valueOf(position.get(0)));
            }
            return getCoordinates(zip);
        } catch (Exception e) {
            log.error("Error during AWS geocoding", e);
            return getCoordinates(zip);
        }
    }
}
