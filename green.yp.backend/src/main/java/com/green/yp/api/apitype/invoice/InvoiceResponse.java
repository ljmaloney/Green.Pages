package com.green.yp.api.apitype.invoice;


import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record InvoiceResponse(UUID invoiceId,
                              UUID paymentTransactionId,
                              String externalRef,
                              InvoiceType invoiceType,
                              OffsetDateTime paidDate,
                              String description,
                              BigDecimal invoiceTotal,
                              String paymentReceiptNumber,
                              String invoiceNumber,
                              String paymentReceiptUrl,
                              List<InvoiceLineItemResponse> lineItems){}