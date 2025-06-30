package com.green.yp.classifieds.data.model;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ClassifiedAdFeature(
    List<String> features, @NotNull Integer maxImages, boolean protectContact) {}
