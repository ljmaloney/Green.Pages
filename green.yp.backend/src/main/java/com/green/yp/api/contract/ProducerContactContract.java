package com.green.yp.api.contract;

import com.green.yp.api.apitype.producer.ProducerContactRequest;
import com.green.yp.api.apitype.producer.ProducerContactResponse;
import com.green.yp.producer.service.ProducerContactOrchestrationService;
import com.green.yp.producer.service.ProducerContactService;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProducerContactContract {

  final ProducerContactService service;

  final ProducerContactOrchestrationService orchestrationService;

  public ProducerContactContract(
      ProducerContactService service, ProducerContactOrchestrationService orchestrationService) {
    this.service = service;
    this.orchestrationService = orchestrationService;
  }

  public ProducerContactResponse createContact(
      ProducerContactRequest createContactRequest,
      @NotNull @NonNull UUID producerId,
      @NotNull @NonNull UUID locationId,
      String ipAddress) {
    return orchestrationService.createContact(
        createContactRequest, producerId, locationId, ipAddress);
  }

  public List<ProducerContactResponse> findContacts(
      @NotNull @NonNull UUID producerId, @NotNull @NonNull UUID locationId) {
    return service.findContacts(producerId, locationId);
  }

  public void cancelAuthentication(
      @NotNull @NonNull UUID producerId, String userId, @NotNull @NonNull String ipAddress) {
    service.cancelAccountAuthentication(producerId, userId, ipAddress);
  }

  public List<ProducerContactResponse> findAdminContacts(UUID producerId) {
    return service.findAdminContacts(producerId);
  }

  public ProducerContactResponse updatePrimaryContact(
      @NotNull @NonNull ProducerContactRequest request,
      @NotNull @NonNull UUID producerId,
      @NotNull @NonNull UUID locationId,
      @NotNull @NonNull String ipAddress) {
    if (request.contactId() != null) {
      return orchestrationService.updateContact(request, producerId, locationId, ipAddress);
    }
    return orchestrationService.createContact(request, producerId, locationId, ipAddress);
  }

  public void deleteContacts(@NotNull @NonNull List<UUID> producerIds) {
    orchestrationService.deleteContacts(producerIds);
  }

  public void validateContact(@NotNull @NonNull UUID accountId, UUID contactId, @NotNull @NonNull String validationToken) {
    service.validateContact(accountId, contactId, validationToken);
  }

  public void validateEmail(@NotNull @NonNull UUID accountId, String email, @NotNull @NonNull String validationToken) {
    service.validateEmail(accountId, email, validationToken);
  }
}
