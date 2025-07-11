package com.green.yp.classifieds.data.model;

import java.math.BigDecimal;

public record ClassifiedSearchProjection(Classified classified,
                                         ClassifiedAdType adType,
                                         ClassifiedCategory category,
                                         BigDecimal distance) {}
