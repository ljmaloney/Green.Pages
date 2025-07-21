package com.green.yp.api.apitype.payment;

import com.green.yp.api.apitype.common.enumeration.PaymentActionType;
import com.green.yp.api.apitype.producer.enumeration.InvoiceCycleType;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

public record ApiProducerPayment(String paymentMethod,
                                 @NotNull @NonNull PaymentActionType actionType,
                                 @NotNull @NonNull InvoiceCycleType cycleType) {}
