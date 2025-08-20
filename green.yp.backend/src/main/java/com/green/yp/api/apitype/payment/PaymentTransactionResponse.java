package com.green.yp.api.apitype.payment;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PaymentTransactionResponse(
    UUID transactionId,
    String paymentRef,
    String locationRef,
    String orderRef,
    String customerRef,
    String receiptNumber,
    String ipAddress,
    String status,
    String errorStatusCode,
    String errorDetail,
    String sourceType,
    BigDecimal amount,
    BigDecimal refundAmount,
    BigDecimal totalAmount,
    String currencyCode,
    String firstName,
    String lastName,
    String address,
    String city,
    String state,
    String postalCode,
    String phoneNumber,
    String emailAddress,
    String statementDescriptionIdentifier,
    String receiptUrl,
    OffsetDateTime createDate) {}
