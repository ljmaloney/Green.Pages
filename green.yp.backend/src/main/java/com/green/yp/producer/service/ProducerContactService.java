package com.green.yp.producer.service;

import com.green.yp.api.apitype.producer.ProducerContactResponse;
import com.green.yp.api.apitype.producer.enumeration.ProducerContactType;
import com.green.yp.exception.BusinessException;
import com.green.yp.exception.ErrorCodeType;
import com.green.yp.exception.NotFoundException;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.producer.data.model.ProducerContact;
import com.green.yp.producer.data.repository.ProducerContactRepository;
import com.green.yp.producer.mapper.ProducerContactMapper;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProducerContactService {

  final ProducerContactMapper producerContactMapper;

  final ProducerContactRepository contactRepository;

  public ProducerContactService(
      ProducerContactMapper producerContactMapper, ProducerContactRepository contactRepository) {
    this.producerContactMapper = producerContactMapper;
    this.contactRepository = contactRepository;
  }

  ProducerContact findActiveContact(@NonNull UUID contactId) {
    ProducerContact contact =
        contactRepository
            .findById(contactId)
            .orElseThrow(() -> new NotFoundException("ProducerContact", contactId));

    if (contact.getProducerContactType() == ProducerContactType.DISABLED) {
      log.error("Producer contact identified by {} is not active", contactId);
      throw new PreconditionFailedException(
          "Producer contact identified by {} is not active", contactId);
    }

    return contact;
  }

  public ProducerContactResponse findContact(
      @NotNull @NonNull UUID contactId, @NotNull @NonNull Boolean activeOnly) {
    log.info("Loading {} contact for contactId {}", activeOnly ? "active" : "", contactId);
    if (activeOnly) {
      return producerContactMapper.fromEntity(findActiveContact(contactId));
    }
    return contactRepository
        .findById(contactId)
        .map(producerContactMapper::fromEntity)
        .orElseThrow(() -> new NotFoundException("Producer Contact", contactId));
  }

  public List<ProducerContactResponse> findContacts(
      @NotNull @NonNull UUID producerId, @NotNull @NonNull UUID locationId) {
    log.info("Loading contacts for producer {} and location {}", producerId, locationId);

    List<ProducerContact> contacts =
        contactRepository.findByProducerIdAndProducerLocationId(producerId, locationId);

    return producerContactMapper.fromEntity(contacts);
  }

  public List<ProducerContactResponse> findContacts(
      @NotNull @NonNull UUID producerId, UUID locationId, @NotNull @NonNull Boolean activeOnly) {
    log.info(
        "Loading {} contacts for producer {} and location {}",
        activeOnly ? "active" : "",
        producerId,
        locationId);

    List<ProducerContact> contacts =
        activeOnly
            ? contactRepository.findProducerContacts(
                producerId,
                locationId,
                List.of(
                    ProducerContactType.PRIMARY, ProducerContactType.ADMIN,
                    ProducerContactType.ACCOUNTS_PAYABLE, ProducerContactType.SALES))
            : contactRepository.findProducerContacts(
                producerId,
                locationId,
                List.of(
                    ProducerContactType.PRIMARY,
                    ProducerContactType.ADMIN,
                    ProducerContactType.DISABLED,
                    ProducerContactType.ACCOUNTS_PAYABLE,
                    ProducerContactType.SALES));

    return producerContactMapper.fromEntity(contacts);
  }

  public void cancelAccountAuthentication(
      @NotNull @NonNull UUID producerId, String userId, @NotNull @NonNull String ipAddress) {
    log.info(
        "Canceling account, disabling authentication and contacts for {} by user {} from ipAddress {}",
        producerId,
        userId,
        ipAddress);
    // authentication being disabled for account cancellation.
    // authentication available until auth_cancel_date (current time +2 months minus 1 second)
    OffsetDateTime cancelDate =
        OffsetDateTime.now().plusMonths(2).truncatedTo(ChronoUnit.DAYS).minusSeconds(1);

    int contactsImpacted =
        contactRepository.cancelProducerContact(producerId, OffsetDateTime.now(), cancelDate);

    log.info(
        "Cancelled {} producerContacts for {}, last date for authentication {}",
        contactsImpacted,
        producerId,
        cancelDate);
  }

  public List<ProducerContactResponse> findAdminContacts(UUID producerId) {
    List<ProducerContact> contacts =
        contactRepository.findProducerContacts(
            producerId, ProducerContactType.ADMIN, ProducerContactType.PRIMARY);
    return producerContactMapper.fromEntity(contacts);
  }

  public void validateContact(
      @NotNull @NonNull UUID producerId,
      @NotNull @NonNull UUID contactId,
      @NotNull @NonNull String validationToken) {
    contactRepository
        .findById(contactId)
        .ifPresentOrElse(
            contact -> {
              if (contact.isValidEmailToken(validationToken)) {
                contact.setEmailConfirmed(true);
                contact.setEmailConfirmedDate(OffsetDateTime.now());
                contactRepository.save(contact);
              } else {
                log.warn("Invalid email token {} for {}", validationToken, contactId);
                throw new BusinessException(
                    "Invalid email validation token",
                    HttpStatus.BAD_REQUEST,
                    ErrorCodeType.BUSINESS_VALIDATION_ERROR);
              }
            },
            () -> {
              log.warn("No contact found for {}", contactId);
              throw new NotFoundException("No contact found for " + contactId);
            });
  }

  public void validateEmail(
      @NotNull @NonNull UUID accountId,
      @NotNull @NonNull String email,
      @NotNull @NonNull String validationToken) {
    contactRepository
        .findByProducerIdAndEmailAddress(accountId, email)
        .ifPresentOrElse(
            contact -> {
              if (contact.isValidEmailToken(validationToken)) {
                contact.setEmailConfirmed(true);
                contact.setEmailConfirmedDate(OffsetDateTime.now());
                contactRepository.save(contact);
              } else {
                log.warn("Invalid email token {} for {}", validationToken, email);
                throw new BusinessException(
                    "Invalid email validation token",
                    HttpStatus.BAD_REQUEST,
                    ErrorCodeType.BUSINESS_VALIDATION_ERROR);
              }
            },
            () -> {
              log.warn("No contact found for {}", email);
              throw new NotFoundException("No contact found for " + email);
            });
  }
}
