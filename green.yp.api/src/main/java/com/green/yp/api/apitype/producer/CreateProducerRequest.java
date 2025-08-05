package com.green.yp.api.apitype.producer;

import com.green.yp.api.apitype.producer.enumeration.InvoiceCycleType;
import com.green.yp.api.apitype.producer.enumeration.ProducerSubscriptionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

import java.util.UUID;

public record CreateProducerRequest(
    @NotNull
        @NonNull
        @Size(max = 100, message = "The business name must be less than 100 characters in length")
        String businessName,
    @NonNull
        @NotNull
        @Size(max = 50, message = "The line of business must be less than 50 characters in length")
        UUID lineOfBusinessId,
    @NotNull @NonNull UUID subscriptionId,
    @NotNull @NonNull ProducerSubscriptionType subscriptionType,
    InvoiceCycleType invoiceCycleType,
    @Size(max = 200, message = "The website URL must be less than 200 characters in length")
        String websiteUrl,
    @Size(max = 512) String keywords,
    @Size(max = 512) String narrative) {}
