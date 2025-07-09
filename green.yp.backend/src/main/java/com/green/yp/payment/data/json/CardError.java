package com.green.yp.payment.data.json;

import com.squareup.square.types.ErrorCategory;
import com.squareup.square.types.ErrorCode;
import java.util.Map;
import java.util.Optional;

public record CardError(
    ErrorCategory category,
    ErrorCode code,
    Optional<String> detail,
    Optional<String> field,
    Map<String, Object> additionalProperties) {}
