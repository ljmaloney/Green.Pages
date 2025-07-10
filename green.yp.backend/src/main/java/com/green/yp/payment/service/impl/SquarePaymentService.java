package com.green.yp.payment.service.impl;

import com.green.yp.api.apitype.payment.PaymentRequest;
import com.green.yp.api.apitype.payment.PaymentResponse;
import com.green.yp.payment.service.PaymentService;
import com.squareup.square.SquareClient;
import com.squareup.square.types.*;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "greenyp.payment.service.impl", havingValue = "square",
        matchIfMissing = true)
public class SquarePaymentService implements PaymentService {

    private final SquareClient squareClient;
    private final SquareResponseMapper responseMapper;

    public SquarePaymentService(SquareClient squareClient, SquareResponseMapper responseMapper) {
        this.squareClient = squareClient;
        this.responseMapper = responseMapper;
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest paymentRequest, UUID paymentTransactionId, Optional<String> customerRef) {
        var squarePayment = CreatePaymentRequest.builder()
                .sourceId(paymentRequest.paymentToken())
                        .idempotencyKey(paymentTransactionId.toString())
                .autocomplete(true)
                .referenceId(paymentRequest.referenceId())
                .verificationToken(paymentRequest.verificationToken())
                .note(paymentRequest.note())
                .statementDescriptionIdentifier(StringUtils.truncate(paymentRequest.statementDescription(), 20))
                .buyerEmailAddress(paymentRequest.emailAddress())
                .buyerPhoneNumber(paymentRequest.phoneNumber())
                .amountMoney(createMoney(paymentRequest.paymentAmount()))
                .appFeeMoney(createMoney(BigDecimal.ZERO))
                .tipMoney(createMoney(BigDecimal.ZERO))
                .billingAddress(Address.builder()
                        .addressLine1(paymentRequest.address())
                        .locality(paymentRequest.city())
                        .administrativeDistrictLevel1(paymentRequest.state())
                        .postalCode(paymentRequest.postalCode())
                        .firstName(paymentRequest.firstName())
                        .lastName(paymentRequest.lastName())
                        .build());

        customerRef.ifPresent(squarePayment::customerId);

        var squarePaymentResponse = squareClient.payments().create(squarePayment.build());

        return squarePaymentResponse.getPayment()
                .map(payment -> responseMapper.toPaymentResponse(payment))
                .orElseThrow();
    }

    private Money createMoney(BigDecimal amount) {
        return Money.builder()
                .amount(convertToCents(amount).longValue())
                .currency(Currency.USD)
                .build();
    }

}
