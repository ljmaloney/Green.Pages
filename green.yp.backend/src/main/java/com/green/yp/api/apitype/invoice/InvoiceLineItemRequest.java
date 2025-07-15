package com.green.yp.api.apitype.invoice;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record InvoiceLineItemRequest(int lineItemNumber,
                                     String externalRef1,
                                     String externalRef2,
                                     String description,
                                     BigDecimal amount) {}
