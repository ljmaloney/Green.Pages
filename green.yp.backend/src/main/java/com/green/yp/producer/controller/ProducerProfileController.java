package com.green.yp.producer.controller;

import com.green.yp.api.apitype.producer.ProducerProfileResponse;
import com.green.yp.api.apitype.search.TruncatedProducerResponse;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.producer.service.ProducerProfileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Validated
@RequestMapping("producer")
@Tag(name = "Endpoints for accessing producer profile for UI display")
public class ProducerProfileController {

  private final ProducerProfileService producerProfileService;

  public ProducerProfileController(ProducerProfileService producerProfileService) {
    this.producerProfileService = producerProfileService;
  }

  @GetMapping(path = "profile/{producerLocationId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseApi<ProducerProfileResponse> getProducerProfile(
      @PathVariable("producerLocationId") UUID producerLocationId) {
    return new ResponseApi<>(producerProfileService.getProducerProfile(producerLocationId), null);
  }

  @GetMapping(path = "profile/{producerId}/location/{producerLocationId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseApi<ProducerProfileResponse> getProducerProfile( @PathVariable("producerId") UUID producerId,
                                                           @PathVariable("producerLocationId") UUID producerLocationId) {
    return new ResponseApi<>(producerProfileService.getProducerProfile(producerLocationId), null);
  }

  @GetMapping(path = "profiles", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseApi<List<TruncatedProducerResponse>> getProducerProfile(
      @RequestParam(value = "lineOfBusinessId") UUID lobId,
      @RequestParam(value = "lobUrl") String lobUrl,
      @RequestParam(name = "mostRecent", defaultValue = "true") Boolean mostRecent,
      @RequestParam(name = "number", defaultValue = "6") Integer maxProducers) {
    return new ResponseApi<>(
        producerProfileService.getProfiles(lobId, lobUrl, mostRecent, maxProducers), null);
  }
}
