package com.green.yp.geolocation.service.impl;

import com.green.yp.api.apitype.common.GeocodeLocation;
import com.green.yp.geolocation.service.LiveGeocodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service("openCageGeocodeService")
@Slf4j
@ConditionalOnProperty(name = "greenyp.geocoder.impl", havingValue = "aws")
public class AwsGeocodeServiceImpl  implements LiveGeocodeService {
    @Override
    public GeocodeLocation getCoordinates(String zipCode) {
        return null;
    }

    @Override
    public GeocodeLocation getCoordinates(String address, String city, String state, String zip) {
        return null;
    }
}
