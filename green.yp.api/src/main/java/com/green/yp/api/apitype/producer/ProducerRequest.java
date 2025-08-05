package com.green.yp.api.apitype.producer;

import com.green.yp.api.apitype.producer.enumeration.InvoiceCycleType;
import com.green.yp.api.apitype.producer.enumeration.ProducerSubscriptionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.NonNull;

public record ProducerRequest(
    UUID producerId,
    @NotNull
        @NonNull
        @Size(max = 100, message = "The business name must be less than 100 characters in length")
        String businessName,
    @NonNull @NotNull UUID lineOfBusinessId,
    @NotNull @NonNull UUID subscriptionId,
    @NotNull @NonNull ProducerSubscriptionType subscriptionType,
    InvoiceCycleType invoiceCycleType,
    @Size(max = 200, message = "The website URL must be less than 200 characters in length")
        String websiteUrl,
    @Pattern(regexp = "^[a-zA-Z0-9 ,]+$", message = "Keywords can only contain letters, numbers and spaces")
    String keywords,
    @Size(max = 512) String narrative) {}
