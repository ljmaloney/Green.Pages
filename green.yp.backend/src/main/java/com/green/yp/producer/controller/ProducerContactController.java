package com.green.yp.producer.controller;

import com.green.yp.api.apitype.producer.ProducerContactRequest;
import com.green.yp.api.apitype.producer.ProducerContactResponse;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.config.security.AuthUser;
import com.green.yp.config.security.AuthenticatedUser;
import com.green.yp.producer.service.ProducerContactOrchestrationService;
import com.green.yp.producer.service.ProducerContactService;
import com.green.yp.security.IsAnyAuthenticatedUser;
import com.green.yp.util.RequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
@Tag(name = "Endpoints for handling the producer / subscriber contacts")
@RequestMapping("producer")
public class ProducerContactController {

  private final ProducerContactService contactService;

  private final ProducerContactOrchestrationService contactOrchestrationService;

  public ProducerContactController(
      ProducerContactService contactService,
      ProducerContactOrchestrationService contactOrchestrationService) {
    this.contactService = contactService;
    this.contactOrchestrationService = contactOrchestrationService;
  }

  @Operation(summary = "Returns the contact requested via contactId")
  @ApiResponse(responseCode = "200", description = "Producer/Subscriber contact")
  @ApiResponse(responseCode = "404", description = "Contact not found")
  @GetMapping(path = "/contact/{contactId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<ProducerContactResponse> getContacts(
      @PathVariable(value = "contactId") UUID contactId,
      @RequestParam(name = "activeOnly", defaultValue = "false") Boolean activeOnly) {
    return new ResponseApi<>(contactService.findContact(contactId, activeOnly), null);
  }

  @Operation(
      summary = "Return the contacts for a given producer / subscriber with optional filters")
  @ApiResponse(responseCode = "200", description = "list of contacts found")
  @ApiResponse(responseCode = "404", description = "Requested producer/subscriber not found")
  @GetMapping(path = "/{producerId}/contacts", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<List<ProducerContactResponse>> getContacts(
      @PathVariable("producerId") UUID producerId,
      @RequestParam(name = "locationId", required = false) UUID locationId,
      @RequestParam(name = "activeOnly", defaultValue = "true") Boolean activeOnly) {
    return new ResponseApi<>(contactService.findContacts(producerId, locationId, activeOnly), null);
  }

  @IsAnyAuthenticatedUser
  @Operation(summary = "Creates a contact for a given location")
  @PostMapping(
      path = "/location/{locationId}/contact",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<ProducerContactResponse> createContact(
          @PathVariable("locationId") UUID locationId, @AuthUser AuthenticatedUser authenticatedUser,
          @Valid @RequestBody ProducerContactRequest createContactRequest, HttpServletRequest request) throws Exception {
    return new ResponseApi<>(
        contactOrchestrationService.createContact(locationId, createContactRequest, RequestUtil.getRequestIP(request)), null);
  }

  @IsAnyAuthenticatedUser
  @Operation(summary = "Updates an existing contact")
  @PutMapping(
      path = "/contact",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseApi<ProducerContactResponse> updateContact(
      @Valid @RequestBody ProducerContactRequest contactRequest) {
    return new ResponseApi<>(
        contactOrchestrationService.updateContact(
            contactRequest, null, contactRequest.producerLocationId(), RequestUtil.getRequestIP()),
        null);
  }

  @IsAnyAuthenticatedUser
  @Operation(summary = "Updates an existing contact")
  @PutMapping(
      path = "/location/{locationId}/contact",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseApi<ProducerContactResponse> updateContact(
      @PathVariable("locationId") UUID locationId,
      @Valid @RequestBody ProducerContactRequest contactRequest) {
    return new ResponseApi<>(
        contactOrchestrationService.updateContact(
            contactRequest, null, locationId, RequestUtil.getRequestIP()),
        null);
  }

  @IsAnyAuthenticatedUser
  @Operation(summary = "Deletes (inactivates) a contact")
  @DeleteMapping(path = "/contact/{contactId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void disableContact(@PathVariable("contactId") UUID contactId) {
    contactOrchestrationService.disableContact(contactId);
  }
}
