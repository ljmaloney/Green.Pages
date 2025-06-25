package com.green.yp.geolocation.data.repository;

import com.green.yp.geolocation.data.model.PostalCodeGeocode;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostalCodeGeocodeRepository extends JpaRepository<PostalCodeGeocode, String> {
    Optional<PostalCodeGeocode> findFirstByPlaceNameAndState(String placeName, String state);
}
