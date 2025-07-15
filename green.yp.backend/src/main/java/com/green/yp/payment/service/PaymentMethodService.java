package com.green.yp.payment.service;

import com.green.yp.api.apitype.classified.PaymentMethodResponse;
import com.green.yp.api.apitype.payment.PaymentCustomerResponse;
import com.green.yp.api.apitype.payment.PaymentMethodRequest;
import com.green.yp.api.apitype.payment.PaymentSavedCardResponse;
import com.green.yp.exception.NotFoundException;
import com.green.yp.payment.data.repository.PaymentMethodRepository;
import com.green.yp.payment.mapper.PaymentMethodMapper;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentMethodService {

    private final PaymentMethodRepository repository;
    private final PaymentMethodMapper mapper;

    public PaymentMethodService (PaymentMethodRepository repository,
                                 PaymentMethodMapper mapper) {

        this.repository = repository;
        this.mapper = mapper;
    }

    public PaymentMethodResponse createPaymentMethod(PaymentMethodRequest methodRequest,
                                                     PaymentCustomerResponse newCustomer,
                                                     PaymentSavedCardResponse savedPayment) {

        var paymentMethod = mapper.toEntity(methodRequest, newCustomer, savedPayment);

        return mapper.toResponse(repository.saveAndFlush(paymentMethod));
    }

    public PaymentMethodResponse createPaymentMethod(PaymentMethodRequest methodRequest, String externCustRef, PaymentSavedCardResponse savedPayment) {
        var paymentMethod = mapper.toEntity(methodRequest, externCustRef, savedPayment);

        return mapper.toResponse(repository.saveAndFlush(paymentMethod));
    }

    public PaymentMethodResponse findActiveMethod(String referenceId) {

        return repository.findPaymentMethodByReferenceIdAndActive(referenceId, true)
                .map(mapper::toResponse)
                .orElseThrow( () -> {
                    log.warn("No active payment method found for referenceId {}", referenceId);
                    return new NotFoundException("No active payment method found for referenceId " + referenceId);
                });
    }

    public void deactivateExistingCard(UUID paymentMethodId) {
        log.debug("Deactivating existing Card for referenceId {}", paymentMethodId);

        repository.findById(paymentMethodId)
                .ifPresent( paymentMethod1 -> {
            paymentMethod1.setActive(false);
            paymentMethod1.setCancelDate(OffsetDateTime.now());
            repository.saveAndFlush(paymentMethod1);
        });
    }
}
