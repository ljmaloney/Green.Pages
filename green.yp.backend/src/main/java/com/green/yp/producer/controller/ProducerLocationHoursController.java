package com.green.yp.producer.controller;

import com.green.yp.api.apitype.producer.LocationHoursRequest;
import com.green.yp.api.apitype.producer.LocationHoursResponse;
import com.green.yp.common.controller.BaseRestController;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.producer.service.ProducerLocationHoursService;
import com.green.yp.util.RequestUtil;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Validated
@RequestMapping("producer/location")
public class ProducerLocationHoursController extends BaseRestController {

    private final ProducerLocationHoursService locationHoursService;

    public ProducerLocationHoursController(ProducerLocationHoursService locationHoursService) {
        this.locationHoursService = locationHoursService;
    }

    @GetMapping(
            path = "/{locationId}/hours",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<List<LocationHoursResponse>> getLocationHours(
            @PathVariable("locationId") UUID locationId) {
        return new ResponseApi<>(
                locationHoursService.findLocationHours(locationId, null, RequestUtil.getRequestIP()), null);
    }

    @PostMapping(
            path = "/hours",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<LocationHoursResponse> createLocationHours(
            @Valid @RequestBody LocationHoursRequest request) {
        return new ResponseApi<>(
                locationHoursService.createLocationHours(request, null, RequestUtil.getRequestIP()), null);
    }

    @PutMapping(
            path = "/hours",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseApi<LocationHoursResponse> updateLocationHours(
            @Valid @RequestBody LocationHoursRequest hoursRequest) {
        return new ResponseApi<>(
                locationHoursService.updateLocationHours(hoursRequest, null, RequestUtil.getRequestIP()),
                null);
    }

    @DeleteMapping(path = "/hours/{locationHoursId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocationHours(@PathVariable("locationHoursId") UUID locationHoursId) {
        locationHoursService.deleteLocationHours(locationHoursId, null, RequestUtil.getRequestIP());
    }

    @DeleteMapping(path = "{locationId}/hours")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllLocationHours(@PathVariable("locationId") UUID locationId) {
        locationHoursService.deleteAllHours(locationId, null, RequestUtil.getRequestIP());
    }
}
