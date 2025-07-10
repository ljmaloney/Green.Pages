package com.green.yp.payment.data.json;

import java.util.Map;

public record CardPaymentTimeline(
    String authorizedAt,
    String capturedAt,
    String voidedAt,
    Map<String, Object> additionalProperties) {}
