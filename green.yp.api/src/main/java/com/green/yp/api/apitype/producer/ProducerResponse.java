package com.green.yp.api.apitype.producer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.green.yp.api.apitype.producer.enumeration.InvoiceCycleType;
import com.green.yp.api.apitype.producer.enumeration.ProducerSubscriptionType;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProducerResponse(
    @NonNull @NotNull UUID producerId,
    @NotNull @NonNull OffsetDateTime createDate,
    @NotNull @NonNull OffsetDateTime lastUpdateDate,
    OffsetDateTime termsAndConditionsDate,
    @NotNull @NonNull String businessName,
    OffsetDateTime cancelDate,
    OffsetDateTime lastBillDate,
    OffsetDateTime lastBillPaidDate,
    UUID lineOfBusinessId,
    @NotNull @NonNull ProducerSubscriptionType subscriptionType,
    InvoiceCycleType invoiceCycleType,
    String websiteUrl,
    String iconLink,
    List<ProducerSubscriptionResponse> subscriptions,
    String narrative) {}
