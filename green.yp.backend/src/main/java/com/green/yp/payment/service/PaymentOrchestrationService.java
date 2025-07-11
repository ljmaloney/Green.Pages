package com.green.yp.payment.service;

import com.green.yp.api.apitype.payment.PaymentRequest;
import com.green.yp.api.apitype.payment.PaymentTransactionResponse;
import java.util.Optional;

import com.squareup.square.core.SquareApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentOrchestrationService {

    private final PaymentTransactionService transactionService;
    private final PaymentService paymentService;

    public PaymentOrchestrationService(PaymentTransactionService transactionService,
                                       PaymentService paymentService) {
        this.transactionService =  transactionService;
        this.paymentService = paymentService;
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
