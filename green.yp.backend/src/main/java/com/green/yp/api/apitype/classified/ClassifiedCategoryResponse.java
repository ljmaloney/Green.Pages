package com.green.yp.api.apitype.classified;

import java.util.UUID;

public record ClassifiedCategoryResponse(
    UUID categoryId,
    Boolean active,
    String name,
    String urlName,
    String shortDescription,
    String description) {}
