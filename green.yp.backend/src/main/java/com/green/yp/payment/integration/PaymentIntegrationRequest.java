package com.green.yp.payment.integration;

import com.green.yp.api.apitype.payment.ProducerPaymentMethodResponse;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.NonNull;

public record PaymentIntegrationRequest(
    @NotNull @NonNull UUID invoiceId,
    @NotNull @NonNull String invoiceNumber,
    @NotNull @NonNull String descriptionText,
    @NotNull @NonNull BigDecimal invoiceAmount,
    @NotNull @NonNull ProducerPaymentMethodResponse paymentMethod) {}
