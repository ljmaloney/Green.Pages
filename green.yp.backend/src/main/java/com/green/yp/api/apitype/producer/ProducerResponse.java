package com.green.yp.api.apitype.producer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.green.yp.api.apitype.enumeration.CancelReasonType;
import com.green.yp.api.apitype.producer.enumeration.InvoiceCycleType;
import com.green.yp.api.apitype.producer.enumeration.ProducerSubscriptionType;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProducerResponse(
    @NonNull @NotNull UUID producerId,
    @NotNull @NonNull OffsetDateTime createDate,
    @NotNull @NonNull OffsetDateTime lastUpdateDate,
    OffsetDateTime termsAndConditionsDate,
    @NotNull @NonNull String businessName,
    OffsetDateTime cancelDate,
    CancelReasonType cancelReason,
    OffsetDateTime lastBillDate,
    OffsetDateTime lastBillPaidDate,
    UUID lineOfBusinessId,
    @NotNull @NonNull ProducerSubscriptionType subscriptionType,
    InvoiceCycleType invoiceCycleType,
    String websiteUrl,
    String iconLink,
    String keywords,
    List<ProducerSubscriptionResponse> subscriptions,
    String narrative) {}
