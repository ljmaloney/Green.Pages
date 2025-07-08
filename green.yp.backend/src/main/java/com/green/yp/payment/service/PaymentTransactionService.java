package com.green.yp.payment.service;

import com.green.yp.api.apitype.payment.PaymentRequest;
import com.green.yp.payment.data.model.PaymentTransaction;
import com.green.yp.payment.data.repository.PaymentTransactionRepository;
import com.green.yp.payment.mapper.PaymentTransactionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PaymentTransactionService {
    private PaymentTransactionRepository repository;
    private PaymentTransactionMapper mapper;

    public PaymentTransactionService(PaymentTransactionRepository repository,
                                     PaymentTransactionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    @Transactional
    public PaymentTransaction createPaymentRecord(PaymentRequest paymentRequest) {
        log.info("createing new payment record for token");
        var  transaction = mapper.toEntity(paymentRequest);
        return repository.save(transaction);
    }
}
