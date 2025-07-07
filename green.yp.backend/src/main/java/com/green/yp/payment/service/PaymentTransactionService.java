package com.green.yp.payment.service;

import com.green.yp.api.apitype.payment.PaymentRequest;
import com.green.yp.api.apitype.payment.PaymentTransactionResponse;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentTransactionService {
    public PaymentTransactionResponse applyPayment(PaymentRequest paymentRequest, Optional<String> customerRef) {
        return null;
    }
}
