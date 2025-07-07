package com.green.yp.payment.data.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CardDetails(
        String status,
        Card card,
        @JsonProperty("entry_method") String entryMethod,
        @JsonProperty("cvv_status") String cvvStatus,
        @JsonProperty("avs_status") String avsStatus,
        @JsonProperty("auth_result_code") String authResultCode,
        @JsonProperty("statement_description") String statementDescription,
        @JsonProperty("card_payment_timeline") CardPaymentTimeline cardPaymentTimeline
) {}
