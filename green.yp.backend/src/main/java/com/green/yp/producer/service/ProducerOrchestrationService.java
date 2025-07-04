package com.green.yp.producer.service;

import com.green.yp.api.AuditRequest;
import com.green.yp.api.apitype.enumeration.AuditActionType;
import com.green.yp.api.apitype.enumeration.AuditObjectType;
import com.green.yp.api.apitype.producer.*;
import com.green.yp.api.apitype.producer.enumeration.InvoiceCycleType;
import com.green.yp.api.apitype.producer.enumeration.ProducerSubscriptionType;
import com.green.yp.api.contract.LineOfBusinessContract;
import com.green.yp.api.contract.SubscriptionContract;
import com.green.yp.exception.BusinessException;
import com.green.yp.exception.ErrorCodeType;
import com.green.yp.exception.NotFoundException;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.producer.data.model.Producer;
import com.green.yp.producer.data.model.ProducerLineOfBusiness;
import com.green.yp.producer.data.record.ProducerSubscriptionRecord;
import com.green.yp.producer.data.repository.ProducerLobRepository;
import com.green.yp.producer.data.repository.ProducerRepository;
import com.green.yp.producer.data.repository.ProducerSubscriptionRepository;
import com.green.yp.producer.mapper.ProducerMapper;
import com.green.yp.reference.dto.LineOfBusinessDto;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ProducerOrchestrationService {

  private static final String PRODUCER_ID = "ProducerId";

  final LineOfBusinessContract lobContract;

  final ProducerMapper producerMapper;

  final ProducerRepository producerRepository;

  final ProducerLobRepository producerLobRepository;

  final ProducerSubscriptionRepository subscriptionRepository;

  final SubscriptionContract subscriptionContract;

  private final ProducerSubscriptionService subscriptionService;

  public ProducerOrchestrationService(
      LineOfBusinessContract lobContract,
      ProducerMapper producerMapper,
      ProducerRepository producerRepository,
      ProducerLobRepository producerLobRepository,
      ProducerSubscriptionRepository subscriptionRepository,
      SubscriptionContract subscriptionContract,
      ProducerSubscriptionService subscriptionService) {
    this.lobContract = lobContract;
    this.producerMapper = producerMapper;
    this.producerRepository = producerRepository;
    this.producerLobRepository = producerLobRepository;
    this.subscriptionRepository = subscriptionRepository;
    this.subscriptionContract = subscriptionContract;
    this.subscriptionService = subscriptionService;
  }

  @AuditRequest(
      requestParameter = "newProducer",
      objectType = AuditObjectType.PRODUCER,
      actionType = AuditActionType.CREATE)
  public ProducerResponse createProducer(CreateProducerRequest newProducer, String ipAddress) {
    log.info("Creating new producer from ipAddress: {} - producer: {}", ipAddress, newProducer);

    // validate line of business
    LineOfBusinessDto lobDto = lobContract.findLineOfBusiness(newProducer.lineOfBusinessId());

    Producer producer = producerMapper.toEntity(newProducer);

    producer.addLineOfBusiness(
        ProducerLineOfBusiness.builder()
            .producer(producer)
            .producerId(producer.getId())
            .lineOfBusinessId(newProducer.lineOfBusinessId())
            .primaryLob(true)
            .build());

    subscriptionService.updateSubscription(producer, newProducer.subscriptionId(), newProducer.invoiceCycleType());

    Producer savedProducer = producerRepository.saveAndFlush(producer);

    return producerMapper.fromEntity(savedProducer, lobDto);
  }

  @AuditRequest(
      requestParameter = "producerUpdate",
      objectType = AuditObjectType.PRODUCER,
      actionType = AuditActionType.UPDATE)
  public ProducerResponse updateProducer(ProducerRequest producerUpdate) {
    log.info(
        "Updating producer %s identified by %s",
        producerUpdate.businessName(), producerUpdate.producerId());

    Producer producer =
        producerRepository
            .findById(producerUpdate.producerId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        ErrorCodeType.PRODUCER_NOT_FOUND, producerUpdate.producerId()));
    validateNotCancelled(producer);

    // validate line of business
    LineOfBusinessDto lobDto = lobContract.findLineOfBusiness(producerUpdate.lineOfBusinessId());

    producer.setName(producerUpdate.businessName());
    producer.setNarrative(producerUpdate.narrative());
    producer.setWebsiteUrl(producerUpdate.websiteUrl());
    producer.setSubscriptionType(
        ProducerSubscriptionType.valueOf(producerUpdate.subscriptionType().name()));

    ProducerLineOfBusiness producerLob =
        producer.getLinesOfBusiness().stream()
            .filter(ProducerLineOfBusiness::getPrimaryLob)
            .findFirst()
            .orElseThrow(
                () ->
                    new BusinessException(
                        String.format(
                            "Producer %s missing primary line of business",
                            producerUpdate.producerId())));

    if (!producerLob.getLineOfBusinessId().equals(producerUpdate.lineOfBusinessId())) {
      producerLob.setPrimaryLob(false);
      producer.addLineOfBusiness(
          ProducerLineOfBusiness.builder()
              .producer(producer)
              .producerId(producer.getId())
              .lineOfBusinessId(producerUpdate.lineOfBusinessId())
              .primaryLob(true)
              .build());
    }

    subscriptionService.updateSubscription(
        producer,
        producerUpdate.subscriptionId(),
        producerUpdate.invoiceCycleType() != null
            ? InvoiceCycleType.valueOf(producerUpdate.invoiceCycleType().name())
            : null);

    Producer savedProducer = producerRepository.saveAndFlush(producer);
    log.info("Updated producer/subscriber record for {}", savedProducer.getId());
    return producerMapper.fromEntity(savedProducer, lobDto);
  }

  Producer findActiveProducer(@NonNull UUID producerId) {
    Producer producer =
        producerRepository
            .findById(producerId)
            .orElseThrow(() -> new NotFoundException(PRODUCER_ID, producerId));

    if (producer.getCancelDate() != null
        && producer.getCancelDate().isAfter(OffsetDateTime.now())) {
      log.error(
          "Producer identified by {} was cancelled as of {}", producerId, OffsetDateTime.now());
      throw new PreconditionFailedException(
          "Producer identified by {} was cancelled as of {}", producerId, OffsetDateTime.now());
    }
    return producer;
  }



  public ProducerResponse findProducer(@NonNull @NotNull UUID producerId) {
    log.info("Loading producer/account data for {}", producerId);

    Producer producer =
        producerRepository
            .findById(producerId)
            .orElseThrow(() -> new NotFoundException(PRODUCER_ID, producerId));

    ProducerLineOfBusiness primaryLob =
        producer.getLinesOfBusiness().stream()
            .filter(ProducerLineOfBusiness::getPrimaryLob)
            .findFirst()
            .orElseThrow(
                () -> {
                  log.error("Primary Line of business missing for {}", producerId);
                  return new BusinessException(
                      String.format("Primary Line of business missing for %s", producerId));
                });

    List<ProducerSubscriptionRecord> subscriptions =
        subscriptionRepository.findAllSubscriptions(producerId);

    return producerMapper.fromEntity(
        producer, primaryLob, producerMapper.fromRecord(subscriptions));
  }

  public List<ProducerResponse> findProducers(@NotNull @NonNull String websiteUrl) {
    log.info("Searching for producer using website Url {}", websiteUrl);
    try {
      URI uri = new URI(websiteUrl);
      String hostname = uri.getHost();
      List<Producer> producers = producerRepository.findByHostname(hostname);
      return producerMapper.fromEntity(producers);
    } catch (URISyntaxException e) {
      log.error("Invalid URL format for {}", websiteUrl, e);
      throw new BusinessException(
          "Invalid URL format for %s",
          HttpStatus.BAD_REQUEST, ErrorCodeType.BUSINESS_VALIDATION_ERROR, websiteUrl);
    }
  }

  public List<ProducerSubscriptionResponse> findProducerSubscriptions(
      @NonNull @NotNull UUID producerId) {
    log.info("Loading producer subscriptions for {}", producerId);

    producerRepository
        .findById(producerId)
        .orElseThrow(() -> new NotFoundException(PRODUCER_ID, producerId));

    List<ProducerSubscriptionRecord> subscriptions =
        subscriptionRepository.findAllSubscriptions(producerId);

    return producerMapper.fromRecord(subscriptions);
  }

  public ProducerResponse activateProducer(
      UUID producerId,
      OffsetDateTime lastInvoiceDate,
      OffsetDateTime subscriptionPaidDate,
      String userId,
      String ipAddress) {

    Producer producer =
        producerRepository
            .findById(producerId)
            .orElseThrow(() -> new NotFoundException(PRODUCER_ID, producerId));

    if (producer.getSubscriptionType() == ProducerSubscriptionType.LIVE_ACTIVE) {
      return findProducer(producerId);
    }

    if (producer.getSubscriptionType() == ProducerSubscriptionType.LIVE_UNPAID
        || producer.getSubscriptionType() == ProducerSubscriptionType.LIVE_DISABLED_NONPAYMENT
        || producer.getSubscriptionType() == ProducerSubscriptionType.LIVE_CANCELED
        || producer.getSubscriptionType() == ProducerSubscriptionType.BETA_TESTER) {
      producer.setSubscriptionType(ProducerSubscriptionType.LIVE_ACTIVE);
    }

    producer.setCancelDate(null);
    producer.setLastBillDate(lastInvoiceDate);
    producer.setLastBillPaidDate(subscriptionPaidDate);
    producerRepository.saveAndFlush(producer);

    return findProducer(producerId);
  }

  public List<ProducerResponse> findLastModified(
      Integer daysOld, ProducerSubscriptionType producerSubscriptionType) {
    return producerRepository
        .findLastModified(OffsetDateTime.now().minusDays(daysOld), producerSubscriptionType)
        .stream()
        .map(producerMapper::fromEntity)
        .toList();
  }

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  @AuditRequest(
      requestParameter = "producerIds",
      objectType = AuditObjectType.PRODUCER,
      actionType = AuditActionType.DELETE)
  public void deleteUnpaidProducers(List<UUID> producerIds, String ipAddress) {
    log.info("Deleting unpaid / incomplete producer subscriptions - {}", producerIds);

    subscriptionRepository.deleteProducerSubscriptions(producerIds);

    producerLobRepository.deleteProducerLinesOfBusiness(producerIds);

    producerRepository.delete(producerIds, ProducerSubscriptionType.LIVE_UNPAID);
  }

  private void validateNotCancelled(Producer producer) {
    if (producer.getCancelDate() != null) {
      log.info(
          "Producer {} subscription cancelled as of {}",
          producer.getId(),
          producer.getCancelDate());
      throw new PreconditionFailedException(
          String.format(
              "Subscription for %s cancelled as of %s",
              producer.getId(), producer.getCancelDate()));
    }
  }
}
