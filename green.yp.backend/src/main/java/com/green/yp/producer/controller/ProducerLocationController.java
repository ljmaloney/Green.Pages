package com.green.yp.producer.controller;

import com.green.yp.api.apitype.PatchRequest;
import com.green.yp.api.apitype.producer.LocationRequest;
import com.green.yp.api.apitype.producer.ProducerLocationResponse;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.producer.service.ProducerLocationService;
import com.green.yp.util.RequestUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("producer")
@Tag(name = "Endpoints for managing locations")
public class ProducerLocationController {

    private final ProducerLocationService locationService;

    public ProducerLocationController(ProducerLocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping(
            path = "/location/{locationId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<ProducerLocationResponse> getLocation(
            @PathVariable("locationId") UUID locationId,
            @RequestParam(name = "includeHours", required = false) Boolean includeHours) {
        return new ResponseApi<>(locationService.findLocation(locationId, includeHours), null);
    }

    @GetMapping(
            path = "/{producerId}/locations",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<List<ProducerLocationResponse>> getAllLocations(
            @PathVariable("producerId") UUID producerId,
            @RequestParam(name = "activeOnly", required = false, defaultValue = "false")
            Boolean activeOnly,
            @RequestParam(name = "includeHours", required = false, defaultValue = "false")
            Boolean includeHours) {
        return new ResponseApi<>(
                locationService.findAllLocations(producerId, activeOnly, includeHours), null);
    }

    @PostMapping(
            path = "location",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<ProducerLocationResponse> createLocation(
            @Valid @RequestBody LocationRequest locationRequest) {
        return new ResponseApi<>(
                locationService.createLocation(locationRequest, null, RequestUtil.getRequestIP()), null);
    }

    @PutMapping(
            path = "location",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<ProducerLocationResponse> updateLocation(
            @Valid @RequestBody LocationRequest locationRequest) {
        return new ResponseApi<>(
                locationService.updateLocation(locationRequest, null, RequestUtil.getRequestIP()), null);
    }

    @PatchMapping(
            path = "locations/{locationId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<ProducerLocationResponse> patchLocation(
            @PathVariable("locationId") UUID locationId, @RequestBody PatchRequest patchRequest) {
        return new ResponseApi<>(
                locationService.patchLocation(patchRequest, locationId, null, RequestUtil.getRequestIP()),
                null);
    }

    @DeleteMapping(path = "location/{locationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProducerLocation(@PathVariable(name = "locationId") UUID locationId) {
        locationService.deleteLocation(locationId, null, RequestUtil.getRequestIP());
    }
}
