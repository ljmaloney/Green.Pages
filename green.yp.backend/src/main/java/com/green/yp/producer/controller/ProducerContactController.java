package com.green.yp.producer.controller;

import com.green.yp.api.apitype.producer.ProducerContactRequest;
import com.green.yp.api.apitype.producer.ProducerContactResponse;
import com.green.yp.common.controller.BaseRestController;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.producer.service.ProducerContactOrchestrationService;
import com.green.yp.producer.service.ProducerContactService;
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
public class ProducerContactController extends BaseRestController {

    private final ProducerContactService contactService;

    private final ProducerContactOrchestrationService contactOrchestrationService;

    public ProducerContactController(ProducerContactService contactService,
                                     ProducerContactOrchestrationService contactOrchestrationService) {
        this.contactService = contactService;
        this.contactOrchestrationService = contactOrchestrationService;
    }

    @GetMapping(path = "/contact/{contactId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<ProducerContactResponse> getContacts(@PathVariable("contactId") UUID contactId,
                                                            @RequestParam(name = "activeOnly", defaultValue = "false") Boolean activeOnly) {
        return new ResponseApi<>(contactService.findContact(contactId, activeOnly), null);
    }

    @GetMapping(path = "/{producerId}/contacts",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<List<ProducerContactResponse>> getContacts(@PathVariable("producerId") UUID producerId,
                                                                  @RequestParam(name = "locationId", required = false) UUID locationId,
                                                                  @RequestParam(name = "activeOnly", defaultValue = "true") Boolean activeOnly) {
        return new ResponseApi<>(contactService
                .findContacts(producerId, locationId, activeOnly), null);
    }

    @PostMapping(path = "/location/{locationId}/contact",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<ProducerContactResponse> createContact(@PathVariable("locationId") UUID locationId,
                                                              @Valid @RequestBody ProducerContactRequest createContactRequest) {
        return new ResponseApi<>(contactOrchestrationService
                .createContact(locationId, createContactRequest), null);
    }

    @DeleteMapping(path = "/contact/{contactId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disableContact(@PathVariable("contactId") UUID contactId) {
        contactOrchestrationService.disableContact(contactId);
    }
}
