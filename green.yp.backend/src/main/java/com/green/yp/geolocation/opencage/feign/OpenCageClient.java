package com.green.yp.geolocation.opencage.feign;

import com.green.yp.geolocation.opencage.dto.OpenCageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "openCageClient", configuration = OpenCageFeignConfig.class)
public interface OpenCageClient {

    @GetMapping
    OpenCageResponse geocode(
            @RequestParam("q") String query,
            @RequestParam("key") String apiKey,
            @RequestParam("limit") int limit
    );
}
