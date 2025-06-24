package com.green.yp.geolocation.opencage.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OpenCageResult {
    private Geometry geometry;
    private String formatted;
    private Components components;

    @Data
    public class Geometry {
        private BigDecimal lat;
        private BigDecimal lng;
    }

    @Data
    public class Components {
        private String city;
        private String state;
        private String postcode;
        private String country;
        private String countryCode;
    }

}

