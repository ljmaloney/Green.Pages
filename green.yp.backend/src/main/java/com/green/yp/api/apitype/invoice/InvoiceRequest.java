package com.green.yp.api.apitype.invoice;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record InvoiceRequest(String externalRef,
        UUID paymentTransactionId,
        InvoiceType invoiceType,
        String description,
        OffsetDateTime paidDate,
        String paymentReceiptNumber,
        String paymentReceiptUrl,
        BigDecimal invoiceTotal,
        List<InvoiceLineItemRequest> lineItems) {}
