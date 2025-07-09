package com.green.yp.payment.service;

import com.green.yp.api.apitype.payment.PaymentRequest;
import com.green.yp.api.apitype.payment.PaymentResponse;
import java.math.BigDecimal;
import java.math.BigInteger;

import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;

public interface PaymentService {
    default BigInteger convertToCents(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .toBigInteger();
    }

    PaymentResponse processPayment(PaymentRequest paymentRequest, UUID paymentTransactionId, Optional<String> customerRef);
}
