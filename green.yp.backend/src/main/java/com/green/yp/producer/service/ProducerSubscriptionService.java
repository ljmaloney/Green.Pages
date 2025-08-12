package com.green.yp.producer.service;

import com.green.yp.api.apitype.enumeration.CancelReasonType;
import com.green.yp.api.apitype.producer.enumeration.InvoiceCycleType;
import com.green.yp.api.apitype.producer.enumeration.ProducerSubscriptionType;
import com.green.yp.api.contract.SubscriptionContract;
import com.green.yp.exception.NotFoundException;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.producer.data.model.Producer;
import com.green.yp.producer.data.model.ProducerSubscription;
import com.green.yp.producer.data.record.ProducerSubscriptionRecord;
import com.green.yp.producer.data.repository.ProducerRepository;
import com.green.yp.producer.data.repository.ProducerSubscriptionRepository;
import com.green.yp.reference.data.enumeration.SubscriptionType;
import com.green.yp.reference.dto.SubscriptionDto;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProducerSubscriptionService {

    private static final String PRODUCER_ID = "ProducerId";

    private final ProducerRepository producerRepository;
    private final ProducerSubscriptionRepository subscriptionRepository;
    private final SubscriptionContract subscriptionContract;

    public ProducerSubscriptionService(ProducerRepository producerRepository,
                                       ProducerSubscriptionRepository subscriptionRepository,
                                       SubscriptionContract subscriptionContract){
        this.producerRepository = producerRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionContract = subscriptionContract;
    }

    public void updateSubscription(
            Producer producer, UUID subscriptionId, InvoiceCycleType invoiceCycleType) {

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

        // validate subscription
        SubscriptionDto subscriptionDto = subscriptionContract.findSubscription(subscriptionId);

        if (!subscriptionDto.subscriptionType().isPrimarySubscription()) {
            throw new PreconditionFailedException(
                    "Requested subscription is not a top-level(primary) subscription");
        }

        if ( isSubscriptionNotChanged(producer, subscriptionId)){
            log.info("Subscription was not changed when updating producer {}", producer.getId());
            return;
        }

        if ( producer.getSubscriptionType() == ProducerSubscriptionType.ADMIN ||
             producer.getSubscriptionType() == ProducerSubscriptionType.BETA_TESTER) {
            updateUnpaidSubscription(producer, invoiceCycleType, subscriptionDto);
        } else if (producer.getSubscriptionType() == ProducerSubscriptionType.LIVE_UNPAID) {
            updateUnpaidSubscription(producer, invoiceCycleType, subscriptionDto);
        } else {
            upgradePaidSubscription(producer, subscriptionId, invoiceCycleType, subscriptionDto);
        }
    }

    private void upgradePaidSubscription(Producer producer, UUID subscriptionId, InvoiceCycleType invoiceCycleType, SubscriptionDto subscriptionDto) {
        List<ProducerSubscriptionRecord> subscriptions =
                subscriptionRepository.findActiveSubscriptions(
                        producer.getId(), LocalDate.now(), SubscriptionType.getPrimaries());

        // keep this simple for now .. set enddate one current active to end of billing period
        subscriptions.stream()
                .filter(
                        sub ->
                                !subscriptionId.equals(sub.producerSubscription().getSubscriptionId())
                                && sub.producerSubscription().getEndDate() == null)
                .findFirst()
                .ifPresent(
                        sub -> {
                            OffsetDateTime newEndDate = sub.producerSubscription().getCreateDate();
                            newEndDate =
                                    newEndDate.plusMonths(
                                            sub.producerSubscription().getInvoiceCycleType().getMonths());

                            if (sub.producerSubscription()
                                    .getStartDate()
                                    .isBefore(OffsetDateTime.now().toLocalDate())) {
                                sub.producerSubscription().setEndDate(newEndDate.toLocalDate());
                                subscriptionRepository.save(sub.producerSubscription());
                            } else {
                                subscriptionRepository.delete(sub.producerSubscription());
                            }

                            producer.addSubscription(
                                    ProducerSubscription.builder()
                                            .producerId(producer.getId())
                                            .producer(producer)
                                            .subscriptionId(subscriptionDto.subscriptionId())
                                            .nextInvoiceDate(newEndDate.plusDays(1L).toLocalDate())
                                            .startDate(newEndDate.toLocalDate())
                                            .endDate(null)
                                            .invoiceCycleType(
                                                    invoiceCycleType == null ? InvoiceCycleType.MONTHLY : invoiceCycleType)
                                            .build());
                        });
    }

    private void updateUnpaidSubscription(Producer producer,
                                          InvoiceCycleType invoiceCycleType,
                                          SubscriptionDto subscriptionDto) {
        if (CollectionUtils.isNotEmpty(producer.getSubscriptionList())) {
            subscriptionRepository.deleteAll(producer.getSubscriptionList());
            producer.setSubscriptionList(new ArrayList<>());
        }
        producer.addSubscription(
                ProducerSubscription.builder()
                        .producerId(producer.getId())
                        .producer(producer)
                        .subscriptionId(subscriptionDto.subscriptionId())
                        .nextInvoiceDate(LocalDate.now().plusDays(1L))
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusDays(1))
                        .invoiceCycleType(
                                invoiceCycleType == null ? InvoiceCycleType.MONTHLY : invoiceCycleType)
                        .build());
    }

    public void cancelSubscription(UUID producerId, String userId, String ipAddress) {
        log.info("Cancelling account {} by {} from ipAddress {}", producerId, userId, ipAddress);

        Producer producer =
                producerRepository
                        .findById(producerId)
                        .orElseThrow(() -> new NotFoundException(PRODUCER_ID, producerId));

        // the cancel date is the last day of the current month
        // directory listing remains active and customer access remains available
        OffsetDateTime cancelDate =
                OffsetDateTime.now().plusMonths(1).truncatedTo(ChronoUnit.DAYS).minusSeconds(1);

        producer.setCancelDate(cancelDate);

        producerRepository.saveAndFlush(producer);

        log.info("Cancelled producerId {} name {} on {}", producerId, producer.getName(), cancelDate);
    }

    public void paymentFailedCancellation(@NonNull @NotNull UUID producerId,
                                          @NonNull @NotNull CancelReasonType cancelReasonType,
                                          @NonNull @NotNull OffsetDateTime cancelDate, String cancelReason) {
        log.info("Cancelling account {} for {} - {}", producerId, cancelReasonType, cancelReason);
        producerRepository.findById(producerId)
                .ifPresentOrElse(producer -> {
              producer.setCancelDate(cancelDate);
              producer.setCancelReason(cancelReason);
              producer.setCancelReasonType(cancelReasonType);
              producerRepository.saveAndFlush(producer);
            },
            () -> {
              throw new NotFoundException(PRODUCER_ID, producerId);
            });
    }

    private boolean isSubscriptionNotChanged(Producer producer, UUID newSubscriptionId){
        List<ProducerSubscriptionRecord> subscriptions = subscriptionRepository.findActiveSubscriptions(producer.getId(),
                LocalDate.now(),
                SubscriptionType.getPrimaries());

        return subscriptions.stream()
                .filter( s -> s.producerSubscription().getSubscriptionId().equals(newSubscriptionId))
                .findFirst().map(s -> Boolean.TRUE)
                .orElse(Boolean.FALSE);
    }
}
