package com.green.yp.payment.service;

import com.green.yp.api.apitype.payment.PaymentMethodRequest;
import com.green.yp.api.apitype.payment.PaymentMethodResponse;
import com.green.yp.api.apitype.payment.PaymentSavedCardResponse;
import com.green.yp.exception.NotFoundException;
import com.green.yp.payment.data.enumeration.PaymentMethodStatusType;
import com.green.yp.payment.data.model.PaymentMethod;
import com.green.yp.payment.data.repository.PaymentMethodRepository;
import com.green.yp.payment.mapper.PaymentMethodMapper;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PaymentMethodService {

  private final PaymentMethodRepository repository;
  private final PaymentMethodMapper mapper;

  public PaymentMethodService(PaymentMethodRepository repository, PaymentMethodMapper mapper) {

    this.repository = repository;
    this.mapper = mapper;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public PaymentMethodResponse createTempCustomer(PaymentMethodRequest methodRequest) {
    log.info("Saving initial customer data to payment  method for {}", methodRequest.referenceId());
    PaymentMethod paymentMethod = mapper.toEntity(methodRequest);
    paymentMethod.setStatusType(PaymentMethodStatusType.TEMP);
    return mapper.toResponse(repository.saveAndFlush(paymentMethod));
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public PaymentMethodResponse updateSavedCustomer(
      PaymentMethodResponse method, String externCustRef) {
    log.debug(
        "Updating externCustomer {} to payment  method for {}",
        externCustRef,
        method.paymentMethodId());
    return repository
        .findById(method.paymentMethodId())
        .map(
            storedMethod -> {
              storedMethod.setStatusType(PaymentMethodStatusType.CUSTOMER_CREATED);
              storedMethod.setExternCustRef(externCustRef);
              return mapper.toResponse(repository.saveAndFlush(storedMethod));
            })
        .orElseThrow(() -> new NotFoundException("No payment method found for " + externCustRef));
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public PaymentMethodResponse updateCardOnFile(
      PaymentMethodResponse method, PaymentSavedCardResponse savedPayment) {
    log.debug(
        "Updating record for CCOF {} to payment  method for {}",
        savedPayment.cardRef(),
        method.paymentMethodId());
    return repository
        .findById(method.paymentMethodId())
        .map(
            storedMethod -> {
              storedMethod.setStatusType(PaymentMethodStatusType.CCOF_CREATED);
              storedMethod.setCardRef(savedPayment.cardRef());
              storedMethod.setCardDetails(savedPayment.card());
              return mapper.toResponse(repository.saveAndFlush(storedMethod));
            })
        .orElseThrow(
            () -> new NotFoundException("No payment method found for " + savedPayment.cardRef()));
  }

  public PaymentMethodResponse findActiveMethod(String referenceId) {
    return repository
        .findActiveMethod(referenceId, PaymentMethodStatusType.activeTypes())
        .map(mapper::toResponse)
        .orElseThrow(
            () -> {
              log.warn("No active payment method found for referenceId {}", referenceId);
              return new NotFoundException(
                  "No active payment method found for referenceId " + referenceId);
            });
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public PaymentMethodResponse replaceCustomer(
      PaymentMethodRequest methodRequest, PaymentMethodResponse existingMethod) {
    log.info(
        "Disable existing record and create new record for {}", existingMethod.paymentMethodId());

    deactivateExistingCard(existingMethod.paymentMethodId());

    var newMethod = mapper.toEntity(methodRequest);
    newMethod.setStatusType(PaymentMethodStatusType.CUSTOMER_CREATED);
    newMethod.setExternCustRef(existingMethod.externCustRef());

    return mapper.toResponse(repository.saveAndFlush(newMethod));
  }

  public void deactivateExistingCard(UUID paymentMethodId) {
    log.debug("Deactivating existing Card for referenceId {}", paymentMethodId);
    repository
        .findById(paymentMethodId)
        .ifPresent(
            paymentMethod1 -> {
              paymentMethod1.setStatusType(PaymentMethodStatusType.DISABLED);
              paymentMethod1.setCancelDate(OffsetDateTime.now());
              repository.saveAndFlush(paymentMethod1);
            });
  }

  public PaymentMethodResponse findMethod(String referenceId) {
    return repository.findActiveMethod(referenceId,
                    List.of(PaymentMethodStatusType.TEMP,
                    PaymentMethodStatusType.CCOF_CREATED,
                            PaymentMethodStatusType.CUSTOMER_CREATED))
        .map(mapper::toResponse)
        .orElseThrow(
            () -> {
              log.warn("No active payment method found for referenceId {}", referenceId);
              return new NotFoundException(
                  "Payment method not found for referenceId " + referenceId);
            });
        }

    public void deleteMethod(String referenceId) {
      repository.deletePaymentMethodByReferenceIdAndStatusTypeEquals(referenceId, PaymentMethodStatusType.TEMP);
    }
}
