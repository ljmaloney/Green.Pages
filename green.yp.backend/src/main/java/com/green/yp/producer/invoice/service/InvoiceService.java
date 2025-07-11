package com.green.yp.producer.invoice.service;

import com.green.yp.api.apitype.invoice.ProducerInvoiceResponse;
import com.green.yp.api.apitype.producer.ProducerResponse;
import com.green.yp.api.apitype.producer.ProducerSubscriptionResponse;
import com.green.yp.api.contract.ProducerContract;
import com.green.yp.api.contract.SubscriptionContract;
import com.green.yp.exception.NotFoundException;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.producer.invoice.data.model.ProducerInvoice;
import com.green.yp.producer.invoice.data.model.ProducerInvoiceLineItem;
import com.green.yp.producer.invoice.data.repository.ProducerInvoiceRepository;
import com.green.yp.producer.invoice.mapper.InvoiceMapper;
import com.green.yp.reference.data.enumeration.SubscriptionType;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class InvoiceService {

  private final ProducerContract producerContract;
  private final SubscriptionContract subscriptionContract;
  private final InvoiceMapper invoiceMapper;

  private final ProducerInvoiceRepository invoiceRepository;

  public InvoiceService(
          ProducerContract producerContract,
          InvoiceMapper invoiceMapper,
          ProducerInvoiceRepository producerInvoiceRepository,
          SubscriptionContract subscriptionContract) {
    this.producerContract = producerContract;
    this.invoiceMapper = invoiceMapper;
    this.invoiceRepository = producerInvoiceRepository;
    this.subscriptionContract = subscriptionContract;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public ProducerInvoiceResponse createInvoice(UUID producerId, String requestIP) {
    log.info("Creating new invoice for {} from {}", producerId, requestIP);
    try {
      return findUnpaidInvoice(producerId, requestIP);
    } catch (NotFoundException nfe) {
      return createNewInvoice(producerId, requestIP);
    }
  }

  private ProducerInvoiceResponse createNewInvoice(UUID producerId, String requestIp) {
    ProducerResponse producerResponse = producerContract.findProducer(producerId);

    ProducerSubscriptionResponse primarySubscription =
        producerResponse.subscriptions().stream()
            .filter(sub -> sub.subscriptionType().isPrimarySubscription())
            .findFirst()
            .orElseThrow(
                () ->
                    new PreconditionFailedException(
                        "No primary subscription found for %s", producerId));

    ProducerInvoice producerInvoice =
        ProducerInvoice.builder()
            .producerId(producerId)
            .producerSubscriptionId(primarySubscription.producerSubscriptionId())
            .subscriptionId(primarySubscription.subscriptionId())
            .printedInvoiceId(getNextInvoiceNumber())
            .build();

    int lineItemNumber = 1;

    createLineItem(producerInvoice, primarySubscription, lineItemNumber);

    List<ProducerSubscriptionResponse> addOnSubscriptions =
        producerResponse.subscriptions().stream()
            .filter(sub -> !sub.subscriptionType().isPrimarySubscription())
            .toList();

    addOnSubscriptions.stream()
        .forEach(addOn -> createLineItem(producerInvoice, addOn, producerInvoice.getLineItems().size() + 1));

    final ProducerInvoice savedProducerInvoice = invoiceRepository.saveAndFlush(producerInvoice);

    log.info(
        "Created new invoice {} for producerId {}, total amount: {}",
        savedProducerInvoice.getPrintedInvoiceId(),
        producerId,
        savedProducerInvoice.getInvoiceTotal());

    return invoiceMapper.fromEntity(invoiceRepository.findById(savedProducerInvoice.getId()).get());
  }

  private String getNextInvoiceNumber() {
    OffsetDateTime startDate = OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS);
    OffsetDateTime endDate = startDate.plusDays(1).minusSeconds(1);

    Integer counter = invoiceRepository.getCountByDate(startDate, endDate);

    String prefix = new SimpleDateFormat("yyyyMMdd").format(Date.from(startDate.toInstant()));
    return String.format(
        "%s-%s", prefix, new DecimalFormat("000000").format(counter.doubleValue() + 1));
  }

  public ProducerInvoiceResponse findUnpaidInvoice(UUID producerId, String requestIP) {
    ProducerInvoice unpaidProducerInvoice =
        invoiceRepository
            .findUnpaidSubscriptionInvoice(
                producerId, SubscriptionType.TOP_LEVEL, SubscriptionType.LINE_OF_BUSINESS)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        String.format("No unpaid invoices found for producer %s", producerId)));
    return invoiceMapper.fromEntity(unpaidProducerInvoice);
  }

  private void createLineItem(
          ProducerInvoice producerInvoice, ProducerSubscriptionResponse subscription, int lineItemNumber) {
    producerInvoice.addLineItem(
        ProducerInvoiceLineItem.builder()
            .producerId(producerInvoice.getProducerId())
            .producerInvoiceId(producerInvoice.getId())
            .subscriptionId(subscription.subscriptionId())
            .lineItem(lineItemNumber)
            .description(getLineItemDescription(subscription))
            .amount(subscription.subscriptionAmount())
            .producerInvoice(producerInvoice)
            .build());
  }

  private String getLineItemDescription(ProducerSubscriptionResponse subscription) {
    return switch (subscription.subscriptionType()) {
      case DATA_IMPORT_NO_DISPLAY, TOP_LEVEL, LINE_OF_BUSINESS ->
          String.format(
              "%s - %s",
              subscription.invoiceCycleType().getCycleDescription(),
              subscription.shortDescription());
      case ADD_ON, LINE_OF_BUSINESS_ADD_ON ->
          String.format(
              "%s - Additional Services - %s",
              subscription.invoiceCycleType().getCycleDescription(),
              subscription.shortDescription());
    };
  }

  public ProducerInvoiceResponse findInvoice(UUID invoiceId, String requestIp) {
    return invoiceMapper.fromEntity(
        invoiceRepository
            .findById(invoiceId)
            .orElseThrow(() -> new NotFoundException("Invoice", invoiceId)));
  }

  public ProducerInvoiceResponse markInvoicePaid(
      UUID invoiceId, Optional<UUID> paymentTransactionId, String requestIP) {
    log.info("Marking invoice {} as being paid from {}", invoiceId, requestIP);

    final ProducerInvoice producerInvoice =
        invoiceRepository
            .findById(invoiceId)
            .orElseThrow(() -> new NotFoundException("Invoice", invoiceId));

    producerInvoice.setPaidDate(OffsetDateTime.now());
    //        paymentTransactionId.ifPresent(() -> invoice.set);

    return invoiceMapper.fromEntity(invoiceRepository.save(producerInvoice));
  }

  public List<ProducerInvoiceResponse> findInvoices(
      UUID producerId, LocalDate startDate, LocalDate endDate, Boolean descending) {
    log.info(
        "Loading invoices for {} between start {} and end {}, sorted {}",
        producerId,
        startDate,
        endDate,
        (descending ? "descending" : "ascending"));

    Sort createDateSort = Sort.by(descending ? Sort.Direction.DESC : Sort.Direction.ASC, "createDate");

    return invoiceRepository
        .findByProducerIdAndCreateDateBetween(producerId,
                                    startDate.atStartOfDay().atOffset(ZoneOffset.UTC),
                                    endDate.atTime(23, 59, 59).atOffset(ZoneOffset.UTC),
                                    createDateSort)
            .stream()
            .map( invoice -> invoiceMapper.fromEntity(invoice, subscriptionContract.findSubscription(invoice.getSubscriptionId())))
            .toList();
    }
}
