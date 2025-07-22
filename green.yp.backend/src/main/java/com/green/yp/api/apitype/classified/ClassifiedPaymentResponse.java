package com.green.yp.api.apitype.classified;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ClassifiedPaymentResponse(UUID classifiedId,
                                     @Size(max = 200) @NotNull String classifiedTitle,
                                     String paymentStatus,
                                     String paymentRef,
                                        String orderRef,
                                        String receiptNumber,
                                        String errorStatusCode,
                                        String errorDetail) {
}