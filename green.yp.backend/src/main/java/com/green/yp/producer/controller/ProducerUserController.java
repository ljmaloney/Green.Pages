package com.green.yp.producer.controller;

import com.green.yp.api.apitype.producer.ProducerContactRequest;
import com.green.yp.api.apitype.producer.ProducerCredentialsResponse;
import com.green.yp.api.apitype.producer.UserCredentialsRequest;
import com.green.yp.api.apitype.producer.enumeration.ProducerContactType;
import com.green.yp.api.apitype.producer.enumeration.ProducerDisplayContactType;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.producer.service.ProducerContactOrchestrationService;
import com.green.yp.producer.service.ProducerUserService;
import com.green.yp.security.IsSubscriberAdminOrAdmin;
import com.green.yp.security.IsSubscriberOrAdmin;
import com.green.yp.util.RequestUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Validated
@RequestMapping("producer")
@Tag(name = "endpoints for managing the authorized users of the subscription")
public class ProducerUserController {

  private final ProducerUserService userService;
  private final ProducerContactOrchestrationService contactOrchestrationService;

  public ProducerUserController(
      ProducerUserService userService,
      ProducerContactOrchestrationService contactOrchestrationService) {
    this.userService = userService;
    this.contactOrchestrationService = contactOrchestrationService;
  }

  @IsSubscriberAdminOrAdmin
  @PostMapping(
      path = "/{producerId}/authorize/user",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<ProducerCredentialsResponse> createAuthorizedUser(
      @NotNull @NonNull @PathVariable("producerId") UUID producerId,
      @NotNull @NonNull @Valid @RequestBody UserCredentialsRequest credentialsRequest)
      throws NoSuchAlgorithmException {
    contactOrchestrationService.createContact(
        new ProducerContactRequest(
            null,
            null,
            ProducerContactType.ADMIN,
            ProducerDisplayContactType.NO_DISPLAY,
            null,
            credentialsRequest.firstName(),
            credentialsRequest.lastName(),
            null,
            credentialsRequest.businessPhone(),
            credentialsRequest.cellPhone(),
            credentialsRequest.emailAddress(), false),
        Optional.of(credentialsRequest),
        producerId,
        null,
        RequestUtil.getRequestIP());

    return new ResponseApi<>(
        userService
            .findCredentials(credentialsRequest.userName(), credentialsRequest.emailAddress())
            .get(),
        null);
  }

  @IsSubscriberAdminOrAdmin
  @PutMapping(
      path = "/{producerId}/authorize/user/{credentialsId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<ProducerCredentialsResponse> updateAuthorizedUser(
      @NotNull @NonNull @PathVariable("producerId") UUID producerId,
      @NotNull @NonNull @PathVariable("credentialsId") UUID credentialsId,
      @NotNull @NonNull @Valid @RequestBody UserCredentialsRequest credentialsRequest)
      throws NoSuchAlgorithmException {
    return new ResponseApi<>(
        userService.updateUserCredentials(
            credentialsRequest, producerId, credentialsId, RequestUtil.getRequestIP()),
        null);
  }

  @IsSubscriberOrAdmin
  @GetMapping(path = "/{producerId}/search/users", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<List<ProducerCredentialsResponse>> findProducerUsers(
      @PathVariable UUID producerId,
      @RequestParam(value = "firstName", required = false) String firstName,
      @RequestParam(value = "lastName", required = false) String lastName) {
    log.info("Search producerUsers by firstname : {} and lastName: {}", firstName, lastName);
    return new ResponseApi<>(
        userService.findUsers(producerId, firstName, lastName, RequestUtil.getRequestIP()), null);
  }
}
