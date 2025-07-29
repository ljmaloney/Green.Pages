package com.green.yp.api.apitype.invoice;


import java.math.BigDecimal;
import java.util.UUID;

public record InvoiceLineItemResponse(UUID lineItemId,
                                      String externalRef1,
                                      String externalRef2,
                                      Integer lineNumber,
                                      Integer quantity,
                                      String description,
                                      BigDecimal amount) {
    public Integer getLineNumber(){
        return lineNumber;
    }
    public String getDescription(){
        return description;
    }
    public BigDecimal getAmount(){
        return amount;
    }
}
