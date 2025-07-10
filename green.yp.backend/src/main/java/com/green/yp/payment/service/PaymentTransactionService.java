package com.green.yp.payment.service;

import com.green.yp.api.apitype.payment.PaymentRequest;
import com.green.yp.api.apitype.payment.PaymentResponse;
import com.green.yp.api.apitype.payment.PaymentTransactionResponse;
import com.green.yp.exception.ErrorCodeType;
import com.green.yp.exception.SystemException;
import com.green.yp.payment.data.model.PaymentTransaction;
import com.green.yp.payment.data.repository.PaymentTransactionRepository;
import com.green.yp.payment.mapper.PaymentTransactionMapper;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PaymentTransactionService {
    private final PaymentTransactionRepository repository;
    private final PaymentTransactionMapper mapper;

    public PaymentTransactionService(PaymentTransactionRepository repository,
                                     PaymentTransactionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    @Transactional
    public PaymentTransaction createPaymentRecord(PaymentRequest paymentRequest) {
        log.info("creating new payment record for token");
        var  transaction = mapper.toEntity(paymentRequest);
        return repository.save(transaction);
    }

    @Transactional
    public PaymentTransactionResponse updatePayment(UUID transactionId, PaymentResponse cardResponse) {
        log.info("updating payment record {} for completion with response", transactionId);

        var  transaction = repository.findById(transactionId).orElseThrow(() -> {
            log.error("transaction not found for {}", transactionId);
            return new SystemException("Missing payment transaction record",
                    HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodeType.SYSTEM_ERROR);
        });

         transaction.setPaymentRef(cardResponse.paymentRef());
         transaction.setCustomerRef(cardResponse.customerRef());
         transaction.setLocationRef(cardResponse.locationRef());
         transaction.setStatus(cardResponse.status());
         transaction.setSourceType(cardResponse.sourceType());
         transaction.setReceiptUrl(cardResponse.receiptUrl());
         transaction.setReceiptNumber(cardResponse.receiptNumber());
         transaction.setStatementDescriptionIdentifier(cardResponse.descriptionId());
         transaction.setCurrencyCode("USD");
         transaction.setOrderRef(cardResponse.orderRef());
         transaction.setPaymentDetails(cardResponse.cardDetails());

        return mapper.fromEntity(repository.save(transaction));
    }

    public PaymentTransactionResponse updatePaymentError(UUID transactionId, String errorMessage, int errorCode, String errorBody) {
        log.info("updating payment record {} for completion with response", transactionId);

        var  transaction = repository.findById(transactionId).orElseThrow(() -> {
            log.error("transaction not found for {}", transactionId);
            return new SystemException("Missing payment transaction record",
                    HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodeType.SYSTEM_ERROR);
        });

        transaction.setStatus("PAYMENT_ERROR");
        transaction.setErrorCode(errorCode);
        transaction.setErrorMessage(errorMessage);
        transaction.setErrorBody(errorBody);

        return mapper.fromEntity(repository.save(transaction));
    }
}
