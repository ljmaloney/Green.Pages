package com.green.yp.payment.data.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CardPaymentTimeline(
        @JsonProperty("authorized_at") String authorizedAt,
        @JsonProperty("captured_at") String capturedAt
) {}
