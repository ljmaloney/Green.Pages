package com.green.yp.api.apitype.invoice;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.NonNull;

public record InvoiceResponse(
    @NotNull @NonNull UUID invoiceId,
    @NotNull @NonNull UUID producerId,
    @NotNull @NonNull OffsetDateTime createDate,
    @NotNull @NonNull UUID subscriptionId,
    String subscriptionName,
    OffsetDateTime paidDate,
    @NotNull @NonNull String invoiceNumber,
    @NotNull @NonNull BigDecimal invoiceTotal) {}
