package com.green.yp.payment.service;

import com.green.yp.api.apitype.classified.PaymentMethodResponse;
import com.green.yp.api.apitype.payment.PaymentCustomerResponse;
import com.green.yp.api.apitype.payment.PaymentMethodRequest;
import com.green.yp.api.apitype.payment.PaymentSavedCardResponse;
import com.green.yp.payment.data.model.PaymentMethod;
import com.green.yp.payment.data.repository.PaymentMethodRepository;
import com.green.yp.payment.mapper.PaymentMethodMapper;
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
}
