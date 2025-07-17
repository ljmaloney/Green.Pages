package com.green.yp.api.apitype;

import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.NonNull;

public record CreateSubscriptionFeatureRequest(@NotNull @NonNull String feature,
                                               @NotNull @NonNull Integer sortOrder,
                                               @NotNull @NonNull Boolean display,
                                               @NotNull @NonNull String featureName,
                                               Map<String,Object> configMap) {}
