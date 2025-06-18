package com.green.yp.reference.dto;

import java.util.Map;

public record SubscriptionFeatureDto(String feature,
                                     Integer sortOrder,
                                     Boolean display,
                                     String featureName,
                                     Map<String,Object> configMap) {}
