package com.green.yp.geolocation.opencage.dto;

import java.util.List;
import lombok.Data;

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

