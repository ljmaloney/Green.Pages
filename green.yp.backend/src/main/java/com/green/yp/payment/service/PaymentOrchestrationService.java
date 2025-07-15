package com.green.yp.payment.service;

import com.green.yp.api.apitype.classified.PaymentMethodResponse;
import com.green.yp.api.apitype.payment.*;

import java.util.Optional;
import java.util.UUID;

import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.payment.mapper.PaymentMethodMapper;
import com.squareup.square.core.SquareApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentOrchestrationService {

    private final PaymentTransactionService transactionService;
    private final PaymentService paymentService;
    private final PaymentMethodService methodService;
    private final PaymentMethodMapper mapper;

    public PaymentOrchestrationService(PaymentTransactionService transactionService,
                                       PaymentService paymentService, PaymentMethodService methodService,
                                       PaymentMethodMapper mapper) {
        this.transactionService =  transactionService;
        this.paymentService = paymentService;
        this.methodService = methodService;
        this.mapper = mapper;
    }

    public PaymentMethodResponse createPaymentMethod(PaymentMethodRequest methodRequest) {
        log.info("Creating new payment method for subscriber");
        try{
            UUID paymentMethodId = UUID.randomUUID();

            var newCustomer = paymentService.createCustomer(methodRequest, paymentMethodId);

            var savedPayment = paymentService.createCardOnFile(methodRequest, newCustomer.externCustRef(), newCustomer.idempotencyId());

               return methodService.createPaymentMethod(methodRequest, newCustomer, savedPayment);
        } catch (SquareApiException e){
            log.warn("Error creating new customer / saving card {}", e.getMessage(), e);
            throw new PreconditionFailedException("There was an error when attempting to save the card for the subscription");
        }
    }

    public PaymentTransactionResponse applyPayment(PaymentRequest paymentRequest, Optional<String> customerRef) {
        //first create payment record
        var paymentResponse = transactionService.createPaymentRecord(paymentRequest);
        try{
            //call payment partner API
            var cardResponse = paymentService.processPayment(paymentRequest, paymentResponse.getId(), customerRef);
            //update payment record
            return transactionService.updatePayment(paymentResponse.getId(), cardResponse);
        } catch (SquareApiException e){
            log.warn(e.getMessage(), e);
            return transactionService.updatePaymentError(paymentResponse.getId(), e.getMessage(), e.statusCode(), e.body().toString());
        }
    }
}
