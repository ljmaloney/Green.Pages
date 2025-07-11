package com.green.yp.api.apitype.invoice;

import java.math.BigDecimal;

public record InvoiceLineItemRequest(int lineItemNumber,
                                     String externalRef1,
                                     String externalRef2,
                                     String description,
                                     BigDecimal amount) {}
