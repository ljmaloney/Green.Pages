package com.green.yp.payment.data.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Card(
        @JsonProperty("card_brand") String cardBrand,
        @JsonProperty("last_4") String last4,
        @JsonProperty("exp_month") int expMonth,
        @JsonProperty("exp_year") int expYear,
        String fingerprint,
        @JsonProperty("card_type") String cardType,
        @JsonProperty("prepaid_type") String prepaidType,
        String bin
) {}
