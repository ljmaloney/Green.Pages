package com.green.yp.payment.service;

import com.green.yp.api.apitype.payment.PaymentRequest;
import com.green.yp.api.apitype.payment.PaymentTransactionResponse;
import java.util.Optional;

import com.green.yp.payment.data.model.PaymentTransaction;
import com.green.yp.payment.data.repository.PaymentTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentOrchestrationService {

    private PaymentTransactionService transactionService;
    private PaymentService paymentService;

    public PaymentOrchestrationService(PaymentTransactionService transactionService,
                                       PaymentService paymentService) {
        this.transactionService =  transactionService;
        this.paymentService = paymentService;
    }

    public PaymentTransactionResponse applyPayment(PaymentRequest paymentRequest, Optional<String> customerRef) {
        //first create payment record
        var paymentResponse = transactionService.createPaymentRecord(paymentRequest);

        //call payment partner API
        var cardResponse = paymentService.processPayment(paymentRequest, paymentResponse.getId(), customerRef);

        //update payment record
        return transactionService.updatePayment(paymentResponse.getId(), cardResponse);
    }
}
