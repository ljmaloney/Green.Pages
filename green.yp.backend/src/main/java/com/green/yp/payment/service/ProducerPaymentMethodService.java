package com.green.yp.payment.service;

import com.green.yp.api.apitype.payment.ProducerPaymentMethodRequest;
import com.green.yp.api.apitype.payment.ProducerPaymentMethodResponse;
import com.green.yp.exception.NotFoundException;
import com.green.yp.payment.data.enumeration.PaymentMethodType;
import com.green.yp.payment.data.model.ProducerPaymentMethod;
import com.green.yp.payment.data.repository.ProducerPaymentMethodRepository;
import com.green.yp.payment.mapper.PaymentMapper;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ProducerPaymentMethodService {

  private final ProducerPaymentMethodRepository repository;

  private final PaymentMapper paymentMapper;

  public ProducerPaymentMethodService(
      ProducerPaymentMethodRepository repository, PaymentMapper paymentMapper) {
    this.repository = repository;
    this.paymentMapper = paymentMapper;
  }

  @Transactional
  public ProducerPaymentMethodResponse createPaymentMethod(ProducerPaymentMethodRequest paymentRequest) {
    log.info(
        "Create payment method for {} using {}",
        paymentRequest.producerId(),
        paymentRequest.getMaskedMethod());

    repository
        .findActiveMethod(paymentRequest.producerId())
        .ifPresent(
            method -> {
              log.info(
                  "Replacing existing payment method {} with new payment method", method.getId());
              method.setCancelDate(OffsetDateTime.now());
              method.setActive(false);
              repository.saveAndFlush(method);
            });

    ProducerPaymentMethod method = paymentMapper.toEntity(paymentRequest);
    method.setPaymentType(PaymentMethodType.CREDIT_CARD);
    method.setActive(true);
    if (StringUtils.isBlank(method.getPanLastFour())) {
      method.setPanLastFour(
          method.getPaymentMethod().substring(method.getPaymentMethod().length() - 4));
    }

    return paymentMapper.fromEntity(repository.saveAndFlush(method));
  }

  @Transactional
  public void cancelBilling(
      @NotNull @NonNull UUID producerId, String userId, @NotNull @NonNull String ipAddress) {
    log.info("Cancelling Billing for {} by {} from {}", producerId);

    Optional<ProducerPaymentMethod> optionalPaymentMethod = repository.findActiveMethod(producerId);

    if (optionalPaymentMethod.isEmpty()) {
      log.info("No currently active payment method for cancellation by producerId: {}", producerId);
      return;
    }
    ProducerPaymentMethod producerPaymentMethod = optionalPaymentMethod.get();

    producerPaymentMethod.setCancelDate(OffsetDateTime.now());

    repository.saveAndFlush(producerPaymentMethod);

    log.info("Marked payment method is being cancelled / inactive for {}", producerId);
  }

  public ProducerPaymentMethodResponse findPaymentMethod(UUID paymentMethodId) {
    return repository
        .findById(paymentMethodId)
        .map(paymentMapper::fromEntity)
        .orElseThrow(() -> new NotFoundException("PaymentMethod", paymentMethodId));
  }
}
