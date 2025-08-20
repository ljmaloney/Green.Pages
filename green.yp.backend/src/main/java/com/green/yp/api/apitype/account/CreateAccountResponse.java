package com.green.yp.api.apitype.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.green.yp.api.apitype.producer.ProducerSubscriptionResponse;
import com.green.yp.api.apitype.producer.enumeration.ProducerSubscriptionType;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateAccountResponse(
    @NonNull @NotNull UUID producerId,
    @NotNull @NonNull OffsetDateTime createDate,
    @NotNull @NonNull OffsetDateTime lastUpdateDate,
    OffsetDateTime termsAndConditionsDate,
    @NotNull @NonNull String businessName,
    OffsetDateTime cancelDate,
    OffsetDateTime lastBillDate,
    OffsetDateTime getLastBillPaidDate,
    @NotNull @NonNull UUID lineOfBusinessId,
    @NotNull @NonNull ProducerSubscriptionType subscriptionType,
    String websiteUrl,
    List<ProducerSubscriptionResponse> subscriptions,
    String narrative) {}
