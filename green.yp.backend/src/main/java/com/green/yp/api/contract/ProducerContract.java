package com.green.yp.api.contract;

import com.green.yp.api.apitype.producer.*;
import com.green.yp.api.apitype.producer.enumeration.ProducerSubscriptionType;
import com.green.yp.producer.data.model.ProducerUserCredentials;
import com.green.yp.producer.service.ProducerLocationService;
import com.green.yp.producer.service.ProducerOrchestrationService;
import com.green.yp.producer.service.ProducerSubscriptionService;
import com.green.yp.producer.service.ProducerUserService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.validation.constraints.Size;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ProducerContract {
  private final ProducerOrchestrationService producerService;

  private final ProducerLocationService producerLocationService;

  private final ProducerUserService producerUserService;

  private final ProducerSubscriptionService subscriptionService;

  public ProducerContract(
      ProducerOrchestrationService producerService,
      ProducerLocationService producerLocationService,
      ProducerUserService producerUserService,
      ProducerSubscriptionService subscriptionService) {
    this.producerService = producerService;
    this.producerLocationService = producerLocationService;
    this.producerUserService = producerUserService;
    this.subscriptionService = subscriptionService;
  }

  public ProducerResponse findProducer(@NonNull @NotNull UUID producerId) {
    return producerService.findProducer(producerId);
  }

  public List<ProducerResponse> findProducer(@NonNull @NotNull String webstiteUrl) {
    return producerService.findProducers(webstiteUrl);
  }

  public List<ProducerSubscriptionResponse> findSubscriptions(UUID producerId) {
    return producerService.findProducerSubscriptions(producerId);
  }

  public void cancelSubscription(UUID accountId, String userId, String ipAddress) {
    subscriptionService.cancelSubscription(accountId, userId, ipAddress);
  }

  public ProducerResponse createProducer(CreateProducerRequest producer, String ipAddress) {
    return producerService.createProducer(producer, ipAddress);
  }

  public ProducerResponse activateProducer(
      UUID accountId,
      OffsetDateTime lastInvoiceDate,
      OffsetDateTime subscriptionPaidDate,
      String userId,
      String ipAddress) {
    return producerService.activateProducer(
        accountId, lastInvoiceDate, subscriptionPaidDate, userId, ipAddress);
  }

  public ProducerLocationResponse createLocation(
      LocationRequest createLocationRequest, UUID producerId, String ipAddress) {
    return producerLocationService.createLocation(createLocationRequest, producerId, ipAddress);
  }

  public ProducerCredentialsResponse createMasterUserCredentials(
      @NonNull @NotNull UserCredentialsRequest createUserCredentialsRequest,
      String emailAddress,
      @NonNull @NotNull
      UUID producerId,
      @NonNull @NotNull
      UUID contactId,
      String ipAddress)
      throws NoSuchAlgorithmException {
    return producerUserService.createMasterUserCredentials(
        createUserCredentialsRequest, emailAddress, producerId, contactId, ipAddress);
  }

  public ProducerLocationResponse findPrimaryLocation(UUID accountId) {
    return producerLocationService.findPrimaryLocation(accountId);
  }

  public ProducerResponse updateProducer(ProducerRequest producer) {
    return producerService.updateProducer(producer);
  }

  public ProducerCredentialsResponse findMasterUserCredentials(UUID producerId) {
    return producerUserService.findMasterUserCredentials(producerId);
  }

  public ProducerCredentialsResponse updateMasterCredentials(
      UserCredentialsRequest masterUserCredentials,
      UUID producerId,
      UUID credentialsId,
      String ipAddress)
      throws NoSuchAlgorithmException {
    return producerUserService.updateUserCredentials(
        masterUserCredentials, producerId, credentialsId, ipAddress);
  }

  public ProducerCredentialsResponse replaceMasterUserCredentials(
      UUID producerId,
      UUID credentialsId,
      UserCredentialsRequest masterUserCredentials,
      String ipAddress)
      throws NoSuchAlgorithmException {
    return producerUserService.replaceMasterUserCredentials(
        masterUserCredentials, producerId, ipAddress);
  }

  public List<ProducerResponse> findLastModified(
      Integer daysOld, ProducerSubscriptionType producerSubscriptionType) {
    return producerService.findLastModified(daysOld, producerSubscriptionType);
  }

  public void deleteCredentials(List<UUID> producerIdList) {
    producerUserService.removeCredentials(producerIdList);
  }

  public void deleteProducers(List<UUID> producerIds, String ipAddress) {
    producerService.deleteUnpaidProducers(producerIds, ipAddress);
  }

  public Optional<ProducerCredentialsResponse> findCredential(@NotNull @NonNull
                                                          @NotBlank(message = "Your user name must be between 8 and 150 characters")
                               @Size(min = 8, max = 150, message = "Your user name must be between 8 and 150 characters")
                               String userName,
                               @NonNull @NotBlank String emailAddress) {
    return producerUserService.findCredentials(userName, emailAddress);
  }

  public Optional<ProducerCredentialsResponse> findCredentialByRef(String externalUserRef, String ipAddress) {
    return producerUserService.findCredentialByRef(externalUserRef, ipAddress);
  }
}
