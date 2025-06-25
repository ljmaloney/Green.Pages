package com.green.yp.geolocation.service;

import com.green.yp.api.apitype.common.GeocodeLocation;

/**
 * Marker interface to designate GeocodeService implementations which will attempt to access live
 * geocode services
 */
public interface LiveGeocodeService extends GeocodingService {
    GeocodeLocation getCoordinates(String zipCode);

    GeocodeLocation getCoordinates(String address, String city, String state, String zip);
}
