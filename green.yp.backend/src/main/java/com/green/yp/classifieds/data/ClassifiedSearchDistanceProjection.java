package com.green.yp.classifieds.data;

import java.math.BigDecimal;
import java.util.UUID;

public interface ClassifiedSearchDistanceProjection {
        UUID getClassifiedId();

        String getTitle();

        BigDecimal getDistance();
}
