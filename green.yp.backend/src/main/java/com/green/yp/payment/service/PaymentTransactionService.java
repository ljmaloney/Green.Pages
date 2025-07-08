package com.green.yp.payment.service;

import com.green.yp.payment.data.repository.PaymentTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentTransactionService {
    private PaymentTransactionRepository repository;

    public PaymentTransactionService(PaymentTransactionRepository repository) {
        this.repository = repository;
    }


}
