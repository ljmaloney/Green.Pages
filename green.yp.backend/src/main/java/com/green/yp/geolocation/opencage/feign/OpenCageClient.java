package com.green.yp.geolocation.opencage.feign;

import com.green.yp.geolocation.opencage.dto.OpenCageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "openCageClient",
    url = "${greenyp.geocoder.opencage.url}",
    configuration = OpenCageFeignConfig.class)
public interface OpenCageClient {

  @RequestMapping(method = RequestMethod.GET, path = "/geocode/v1/json")
  OpenCageResponse geocode(
      @RequestParam("q") String query,
      @RequestParam("key") String apiKey,
      @RequestParam("limit") int limit);
}
