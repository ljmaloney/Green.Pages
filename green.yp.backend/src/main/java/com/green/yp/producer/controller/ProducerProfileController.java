package com.green.yp.producer.controller;

import com.green.yp.api.apitype.producer.ProducerProfileResponse;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.producer.service.ProducerOrchestrationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("producer")
@Tag(name = "Endpoints for accessing producer profile for UI display")
public class ProducerProfileController {

    private final ProducerOrchestrationService producerOrchestrationService;

    public ProducerProfileController(ProducerOrchestrationService producerOrchestrationService) {
        this.producerOrchestrationService = producerOrchestrationService;
    }

    @GetMapping(path = "/profile/{producerLocationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseApi<ProducerProfileResponse> getProducerProfile(@PathVariable("producerLocationId") UUID producerLocationId){
        return new ResponseApi<>(producerOrchestrationService.getProducerProfile(producerLocationId), null);
    }
}
