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
import com.green.yp.api.apitype.enumeration.ProducerSubProcessType;
import com.green.yp.producer.data.model.ProducerSubscriptionProcess;
import com.green.yp.producer.data.record.ProducerSubscriptionRecord;
import com.green.yp.producer.data.repository.ProducerLobRepository;
import com.green.yp.producer.data.repository.ProducerRepository;
import com.green.yp.producer.data.repository.ProducerSubProcessRepository;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Limit;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ProducerOrchestrationService {

  private static final String PRODUCER_ID = "ProducerId";
  private static final long MONTH_INCREMENT = 1L;

  @Value("${green.yp.pro.subscription.interval.type:month}")
  private String intervalType;

  @Value("${green.yp.pro.subscription.interval.amount:1}")
  private Integer intervalAmount;

  final LineOfBusinessContract lobContract;

  final ProducerMapper producerMapper;

  final ProducerRepository producerRepository;

  final ProducerLobRepository producerLobRepository;

  final ProducerSubscriptionRepository subscriptionRepository;

  final ProducerSubProcessRepository subProcessRepository;

  final SubscriptionContract subscriptionContract;

  private final ProducerSubscriptionService subscriptionService;

  public ProducerOrchestrationService(
          LineOfBusinessContract lobContract,
          ProducerMapper producerMapper,
          ProducerRepository producerRepository,
          ProducerLobRepository producerLobRepository,
          ProducerSubscriptionRepository subscriptionRepository, ProducerSubProcessRepository subProcessRepository,
          SubscriptionContract subscriptionContract,
          ProducerSubscriptionService subscriptionService) {
    this.lobContract = lobContract;
    this.producerMapper = producerMapper;
    this.producerRepository = producerRepository;
    this.producerLobRepository = producerLobRepository;
    this.subscriptionRepository = subscriptionRepository;
      this.subProcessRepository = subProcessRepository;
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
        "Updating producer {} identified by {}",
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
    producer.setKeywords(producerUpdate.keywords());
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
    log.info("Making producer {} active by user {} from ipAddress {}", producerId, userId, ipAddress);
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
    producer.getSubscriptionList()
            .forEach(subscription -> {
              subscription.setNextInvoiceDate(lastInvoiceDate.plusMonths(MONTH_INCREMENT).toLocalDate());
              subscription.setEndDate(null);
            });

    producerRepository.saveAndFlush(producer);

    return findProducer(producerId);
  }

  public List<ProducerResponse> findLastModified(
          Integer daysOld, ProducerSubscriptionType producerSubscriptionType, int maxRecords) {
    if ( maxRecords > 0 ){
      return producerRepository.findByLastUpdateDateBeforeAndSubscriptionType(OffsetDateTime.now().minusDays(daysOld), producerSubscriptionType, Limit.of(maxRecords))
              .stream().map(producerMapper::fromEntity).toList();
    }
    return producerRepository
        .findLastModified(OffsetDateTime.now().minusDays(daysOld), producerSubscriptionType)
        .stream()
        .map(producerMapper::fromEntity)
        .toList();
  }

  @Transactional
  public ProducerResponse updateBillPaidDate(@NonNull @NotNull UUID producerId,
                                             @NonNull @NotNull OffsetDateTime lastInvoiceDate,
                                             @NonNull @NotNull OffsetDateTime subscriptionPaidDate,
                                             @NonNull @NotNull String userId,
                                             String ipAddress) {
    log.info("Updating bill paid date for {} by user {} from ip address {}", producerId, userId, ipAddress);
    Producer producer =
            producerRepository
                    .findById(producerId)
                    .orElseThrow(() -> new NotFoundException(PRODUCER_ID, producerId));

    producer.setCancelDate(null);
    producer.setSubscriptionType(ProducerSubscriptionType.LIVE_ACTIVE);
    producer.setLastBillDate(lastInvoiceDate);
    producer.setLastBillPaidDate(subscriptionPaidDate);
    producer.getSubscriptionList()
            .forEach( subscription ->{
                      subscription.setNextInvoiceDate(lastInvoiceDate.plusMonths(MONTH_INCREMENT).toLocalDate());
                      subscription.setEndDate(null);
                    });

    producerRepository.saveAndFlush(producer);

    return findProducer(producerId);
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

  @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
  public void initializePaymentProcessQueue() {
    log.debug("Initializing producer / pro subscriptions processing queue");

    log.info("Initializing producer / pro subscriptions interval type {} interval amount {}", intervalType, intervalAmount);
    OffsetDateTime invoiceDate = OffsetDateTime.now();
    if ( "days".equalsIgnoreCase(intervalType)){
      invoiceDate = invoiceDate.minusDays(intervalAmount);
    } else {
      invoiceDate = invoiceDate.minusMonths(intervalAmount);
    }

    log.info("Processing subscription payments for subscribers last paid before {}", invoiceDate);

    List<UUID> subsToProcess = subProcessRepository.getProducersToProcess(invoiceDate);

    subsToProcess.forEach(producer -> {
      subProcessRepository.deleteByProducerId(producer);
      subProcessRepository.saveAndFlush(ProducerSubscriptionProcess.builder()
              .producerId(producer)
              .processStep(ProducerSubProcessType.PREPARE)
              .build());
    });
    log.info("Initialized producer / pro subscriptions processing queue for subscribers last paid before {}", invoiceDate);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
  public List<ProducerResponse> getProducersToProcess(int maxNumberToProcess) {
    List<ProducerSubscriptionProcess> subsToProcess = subProcessRepository.findItemsToProcess(maxNumberToProcess);

    List<UUID> producerIds = subsToProcess.stream()
            .map(ProducerSubscriptionProcess::getProducerId)
            .toList();

    subProcessRepository.updateStatus(producerIds, ProducerSubProcessType.PROCESS_STARTED);

    return producerIds.stream().map(this::findProducer).toList();

  }

  public void updateProcessStatus(@NonNull @NotNull UUID producerId, ProducerSubProcessType producerSubscriptionType) {
    subProcessRepository.findByProducerId(producerId).ifPresent(s -> {
      s.setProcessStep(producerSubscriptionType);
      subProcessRepository.saveAndFlush(s);
    });
    }
}
