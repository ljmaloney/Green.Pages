package com.green.yp.geolocation.data.repository;

import com.green.yp.geolocation.data.model.PostalCodeGeocode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostalCodeGeocodeRepository extends JpaRepository<PostalCodeGeocode, String> {}
