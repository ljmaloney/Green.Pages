package com.green.yp.producer.data.record;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProducerLocationDistanceProjection {
    UUID getProducerId();
    UUID getLocationId();
    String getProducerName();
    BigDecimal getDistance();
}

