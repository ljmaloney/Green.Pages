package com.green.yp.api.apitype.payment;

import com.green.yp.payment.data.json.CardDetails;
import java.util.UUID;

public record PaymentResponse(
    UUID paymentTransactionId,
    String paymentRef,
    String orderRef,
    String locationRef,
    String customerRef,
    String receiptNumber,
    String status,
    String sourceType,
    String descriptionId,
    String receiptUrl,
    String versionToken,
    CardDetails cardDetails) {}
