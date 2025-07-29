package com.green.yp.api.apitype.invoice;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record InvoiceLineItemRequest(int lineItemNumber,
                                     String externalRef1,
                                     String externalRef2,
                                     String description,
                                     Integer quantity,
                                     BigDecimal amount) {}
