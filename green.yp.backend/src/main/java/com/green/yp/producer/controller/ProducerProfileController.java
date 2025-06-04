package com.green.yp.producer.controller;

import com.green.yp.api.apitype.producer.ProducerProfileResponse;
import com.green.yp.api.apitype.search.TruncatedProducerResponse;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.producer.service.ProducerProfileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("producer")
@Tag(name = "Endpoints for accessing producer profile for UI display")
public class ProducerProfileController {

    private final ProducerProfileService producerProfileService;

    public ProducerProfileController(ProducerProfileService producerProfileService) {
        this.producerProfileService = producerProfileService;
    }

    @GetMapping(path = "/profile/{producerLocationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseApi<ProducerProfileResponse> getProducerProfile(@PathVariable("producerLocationId") UUID producerLocationId){
        return new ResponseApi<>(producerProfileService.getProducerProfile(producerLocationId), null);
    }

    @GetMapping(path="/{lineOfBusinessId}/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseApi<List<TruncatedProducerResponse>> getProducerProfile(@PathVariable("lineOfBusinessId") UUID lobId,
                                                                    @RequestParam(name="mostRecent", defaultValue = "true") Boolean mostRecent,
                                                                    @RequestParam(name="number", defaultValue = "6") Integer maxProducers){
        return new ResponseApi<>(producerProfileService.getProfiles(lobId, mostRecent, maxProducers), null);
    }
}
