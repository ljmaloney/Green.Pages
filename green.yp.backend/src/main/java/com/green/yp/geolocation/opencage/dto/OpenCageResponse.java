package com.green.yp.geolocation.opencage.dto;

import lombok.Data;
import java.util.List;

@Data
public class OpenCageResponse {
    private List<OpenCageResult> results;
    private OpenCageStatus status;

    @Data
    public class OpenCageStatus {
        private int code;
        private String message;
    }

}

