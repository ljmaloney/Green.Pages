package com.green.yp.producer.service;

import com.green.yp.api.AuditRequest;
import com.green.yp.api.apitype.enumeration.AuditActionType;
import com.green.yp.api.apitype.enumeration.AuditObjectType;
import com.green.yp.api.apitype.enumeration.EmailTemplateType;
import com.green.yp.api.apitype.producer.ProducerContactRequest;
import com.green.yp.api.apitype.producer.ProducerContactResponse;
import com.green.yp.api.apitype.producer.ProducerLocationResponse;
import com.green.yp.api.apitype.producer.UserCredentialsRequest;
import com.green.yp.api.apitype.producer.enumeration.ProducerContactType;
import com.green.yp.api.contract.EmailContract;
import com.green.yp.exception.NotFoundException;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.exception.SystemException;
import com.green.yp.producer.data.model.ProducerContact;
import com.green.yp.producer.data.model.ProducerLocation;
import com.green.yp.producer.data.repository.ProducerContactRepository;
import com.green.yp.producer.mapper.ProducerContactMapper;
import jakarta.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ProducerContactOrchestrationService {

  final ProducerContactMapper producerContactMapper;

  final ProducerContactRepository contactRepository;

  final ProducerLocationService locationService;

  final ProducerOrchestrationService producerService;

  final ProducerUserService producerUserService;

  private final EmailContract emailContract;

  public ProducerContactOrchestrationService(
      ProducerContactMapper producerContactMapper,
      ProducerContactRepository contactRepository,
      ProducerLocationService locationService,
      ProducerOrchestrationService producerService,
      ProducerUserService producerUserService,
      EmailContract emailContract) {
    this.producerContactMapper = producerContactMapper;
    this.contactRepository = contactRepository;
    this.locationService = locationService;
    this.producerService = producerService;
    this.producerUserService = producerUserService;
    this.emailContract = emailContract;
  }

  public ProducerContactResponse createContact(
      ProducerContactRequest createContactRequest,
      Optional<UserCredentialsRequest> credentialsRequest,
      @NotNull @NonNull UUID producerId,
      UUID locationId,
      String ipAddress) {
    log.info(
        "Attempt to create contact {} for producerId {} and locationId {}",
        StringUtils.isNotBlank(createContactRequest.genericContactName())
            ? createContactRequest.genericContactName()
            : createContactRequest.firstName(),
        producerId,
        locationId);

    ProducerContactResponse contact =
        createContact(createContactRequest, producerId, locationId, ipAddress);

    credentialsRequest.ifPresent(
        credentials -> {
          try {
            producerUserService.createUserCredentials(
                credentials,
                contact.emailAddress(),
                contact.producerId(),
                contact.contactId(),
                ipAddress);
          } catch (NoSuchAlgorithmException e) {
            log.error("Unable to create credentials due to an unexpected error");
            throw new SystemException("Unable to create credentials due to an unexpected error",e);
          }
        });

    return contact;
  }

  @AuditRequest(
      requestParameter = "createContactRequest",
      objectType = AuditObjectType.PRODUCER_CONTACT,
      actionType = AuditActionType.CREATE)
  public ProducerContactResponse createContact(
      @NotNull @NonNull UUID locationId, ProducerContactRequest createContactRequest) {
    log.info(
        "Attempt to create contact {} for locationId {}",
        StringUtils.isNotBlank(createContactRequest.genericContactName())
            ? createContactRequest.genericContactName()
            : createContactRequest.firstName(),
        locationId);
    ProducerLocation location = locationService.findActiveLocation(locationId);
    return createContact(location.getProducerId(), locationId, createContactRequest);
  }

  @AuditRequest(
      requestParameter = "createContactRequest",
      objectType = AuditObjectType.PRODUCER_CONTACT,
      actionType = AuditActionType.CREATE)
  public ProducerContactResponse createContact(
      ProducerContactRequest createContactRequest,
      @NotNull @NonNull UUID producerId,
      UUID locationId,
      String ipAddress) {
    log.info(
        "Attempt to create contact {} for producerId {} and locationId {}",
        StringUtils.isNotBlank(createContactRequest.genericContactName())
            ? createContactRequest.genericContactName()
            : createContactRequest.firstName(),
        producerId,
        locationId);

    producerService.findActiveProducer(producerId);

    ProducerLocationResponse location =
        locationId == null
            ? locationService.findPrimaryLocation(producerId)
            : locationService.findLocation(locationId, false);

    return createContact(producerId, location.locationId(), createContactRequest);
  }

  ProducerContactResponse createContact(
      @NotNull @NonNull UUID producerId,
      @NotNull @NonNull UUID locationId,
      @NotNull @NonNull ProducerContactRequest createContactRequest) {

    if (StringUtils.isBlank(createContactRequest.genericContactName())
        && StringUtils.isBlank(createContactRequest.firstName())
        && StringUtils.isBlank(createContactRequest.lastName())) {
      throw new PreconditionFailedException(
          "Contact must have either a generic name or a firstName + lastName");
    }

    ProducerContact contact = producerContactMapper.toEntity(createContactRequest);
    contact.setProducerId(producerId);
    contact.setProducerLocationId(locationId);
    if (StringUtils.isNotBlank(contact.getEmailAddress())) {
      contact.setEmailConfirmationToken(UUID.randomUUID().toString());
      contact.setEmailConfirmed(false);
    }

    ProducerContact savedContact = contactRepository.saveAndFlush(contact);

    if (StringUtils.isNotBlank(contact.getEmailAddress())) {
      emailContract.sendEmail(
          EmailTemplateType.EMAIL_CONFIRMATION, contact, contact.getEmailAddress());
    }

    log.info(
        "Created contact {} for producerId {} and locationId {}",
        savedContact.getId(),
        producerId,
        locationId);

    return producerContactMapper.fromEntity(savedContact);
  }

  @Transactional
  public void disableContact(@NonNull @NotNull UUID contactId) {
    log.info("Disable producer contact : {}", contactId);

    ProducerContact contact =
        contactRepository
            .findById(contactId)
            .orElseThrow(() -> new NotFoundException("ProducerContact", contactId));

    if (contact.getProducerContactType() == ProducerContactType.DISABLED) {
      // already disabled, return so that controller returns HTTP 204
      log.info("Producer contact {} already disabled", contactId);
      return;
    }
    // disable contact
    contact.setProducerContactType(ProducerContactType.DISABLED);
    contactRepository.save(contact);

    // disable user credentials (if any)
    producerUserService.disableAuthentication(contactId);
  }

  @AuditRequest(
      requestParameter = "request",
      objectType = AuditObjectType.PRODUCER_CONTACT,
      actionType = AuditActionType.UPDATE)
  public ProducerContactResponse updateContact(
      ProducerContactRequest request, UUID producerId, UUID locationId, String ipAddress) {
    log.debug(
        "Updating contact {} for producer {} and location {}",
        request.contactId(),
        producerId,
        locationId);

    if (StringUtils.isBlank(request.genericContactName())
        && StringUtils.isBlank(request.firstName())
        && StringUtils.isBlank(request.lastName())) {
      throw new PreconditionFailedException(
          "Contact must have either a generic name or a firstName + lastName");
    }

    ProducerContact contact =
        contactRepository
            .findById(request.contactId())
            .orElseThrow(() -> new NotFoundException("ProducerContact", request.contactId()));

    contact = producerContactMapper.copy(contact, request);

    return producerContactMapper.fromEntity(contactRepository.saveAndFlush(contact));
  }

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  @AuditRequest(
      requestParameter = "producerIds",
      objectType = AuditObjectType.PRODUCER_LOCATION,
      actionType = AuditActionType.DELETE_LOCATION)
  public void deleteContacts(List<UUID> producerIds) {
    log.info("Deleting contacts for producers {}", producerIds);
    contactRepository.deleteContacts(producerIds);
  }
}
