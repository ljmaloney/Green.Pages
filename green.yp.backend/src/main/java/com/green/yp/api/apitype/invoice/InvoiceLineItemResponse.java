package com.green.yp.api.apitype.invoice;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.NonNull;

public record InvoiceLineItemResponse(
    @NotNull @NonNull UUID lineItemId,
    @NotNull @NonNull UUID invoiceId,
    @NotNull @NonNull OffsetDateTime createDate,
    @NotNull @NonNull Integer lineItem,
    @NotNull @NonNull UUID subscriptionId,
    @NotNull @NonNull BigDecimal amount,
    @NotNull @NonNull String description) {}
