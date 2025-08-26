package com.green.yp.producer.service;

import com.green.yp.api.AuditRequest;
import com.green.yp.api.apitype.enumeration.AuditActionType;
import com.green.yp.api.apitype.enumeration.AuditObjectType;
import com.green.yp.api.apitype.producer.CreateLocationRequest;
import com.green.yp.api.apitype.producer.LocationRequest;
import com.green.yp.api.apitype.producer.ProducerLocationResponse;
import com.green.yp.config.security.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class LocationOrchestrationService {

    private final ProducerLocationService locationService;
    private final ProducerContactOrchestrationService contactService;
    private final ProducerSearchService searchService;

    public LocationOrchestrationService(ProducerLocationService locationService,
                                        ProducerContactOrchestrationService contactService,
                                        ProducerSearchService searchService) {
        this.locationService = locationService;
        this.contactService = contactService;
        this.searchService = searchService;
    }

    @AuditRequest(
            requestParameter = "createLocationRequest",
            objectType = AuditObjectType.PRODUCER_LOCATION,
            actionType = AuditActionType.CREATE)
    @Transactional
    public ProducerLocationResponse createLocation(@Valid CreateLocationRequest createRequest, UUID producerId, String requestIP) {
        log.info("Creating location {} record for producer: {}", createRequest.locationRequest().locationName(), producerId);
        var locationResponse = locationService.createLocation(createRequest, producerId, requestIP);

        createRequest.contactRequest().ifPresent(contactRequest ->
                contactService.createContact(contactRequest, producerId,locationResponse.locationId(), requestIP)
        );

        searchService.upsertProducerLocation(locationResponse.producerId(), locationResponse, requestIP);

        return locationResponse;
    }

    @AuditRequest(
            requestParameter = "updateLocationRequest",
            objectType = AuditObjectType.PRODUCER_LOCATION,
            actionType = AuditActionType.UPDATE)
    public ProducerLocationResponse updateLocation(
            LocationRequest updateLocationRequest, AuthenticatedUser user, String requestIP){
        var response = locationService.updateLocation(updateLocationRequest, user, requestIP);

        searchService.upsertProducerLocation(response.producerId(), response, requestIP);

        return response;
    }
}
