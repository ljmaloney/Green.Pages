package com.green.yp.api.apitype;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import java.util.Map;

public record UpdateSubscriptionFeatureRequest(@NotNull @NonNull String feature,
                                               @NotNull @NonNull Integer sortOrder,
                                               @NotNull @NonNull Boolean display,
                                               @NotNull @NonNull String featureName,
                                               Map<String,Object> configMap) {}
