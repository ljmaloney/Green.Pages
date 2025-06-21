package com.green.yp.producer.controller;

import com.green.yp.api.apitype.PatchRequest;
import com.green.yp.api.apitype.ProducerServiceResponse;
import com.green.yp.api.apitype.producer.ProducerServiceRequest;
import com.green.yp.api.apitype.producer.ProducerServiceUpdateRequest;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.producer.service.ProducerServicesService;
import com.green.yp.util.RequestUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Endpoints for managing services offered by a subscriber/producer")
public class ProducerServicesController {

  private final ProducerServicesService servicesService;

  public ProducerServicesController(ProducerServicesService servicesService) {
    this.servicesService = servicesService;
  }

  @GetMapping(
      path = "/{producerId}/location/{locationId}/services",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<List<ProducerServiceResponse>> getProducerLocationServices(
      @PathVariable(name = "producerId") UUID producerId,
      @PathVariable(name = "locationId") UUID producerLocationId) {
    return new ResponseApi<>(
        servicesService.findServices(producerId, producerLocationId, RequestUtil.getRequestIP()),
        null);
  }

  @PostMapping(
      path = "location/service",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<ProducerServiceResponse> createService(
      @RequestBody ProducerServiceRequest serviceRequest) {
    return new ResponseApi<>(
        servicesService.createService(serviceRequest, null, RequestUtil.getRequestIP()), null);
  }

  @PutMapping(
      path = "location/service",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<ProducerServiceResponse> updateService(
      @RequestBody ProducerServiceUpdateRequest updateRequest) {
    return new ResponseApi<>(
        servicesService.updateService(updateRequest, RequestUtil.getRequestIP()), null);
  }

  @PatchMapping(path = "location/service/{serviceId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<ProducerServiceResponse> patchService(
      @PathVariable(name = "serviceId") UUID serviceId, @RequestBody PatchRequest patchRequest) {
    return new ResponseApi<>(
        servicesService.patchService(serviceId, patchRequest, null, RequestUtil.getRequestIP()),
        null);
  }

  @DeleteMapping(path = "location/service/{serviceId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteService(@PathVariable(name = "serviceId") UUID serviceId) {
    servicesService.deleteService(serviceId, null, RequestUtil.getRequestIP());
  }
}
