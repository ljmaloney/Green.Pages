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

    public PaymentOrchestrationService(PaymentTransactionService transactionService) {
        this.transactionService =  transactionService;
    }

    public PaymentTransactionResponse applyPayment(PaymentRequest paymentRequest, Optional<String> customerRef) {
        //first create payment record
        var paymentResponse = transactionService.createPaymentRecord(paymentRequest);

        //call payment partner API

        //update payment record

        return null;
    }
}
