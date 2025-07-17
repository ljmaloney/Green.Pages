package com.green.yp.api.apitype.payment;

import com.green.yp.payment.data.enumeration.ProducerPaymentType;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.NonNull;

public record ApplyPaymentRequest(
    @NotNull @NonNull UUID invoiceId,
    @NotNull @NonNull UUID producerId,
    ProducerPaymentType paymentType,
    UUID savedPaymentMethodId,
    ProducerPaymentMethodRequest newPaymentMethod) {}
