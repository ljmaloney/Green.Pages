package com.green.yp.search.data.entity;

import java.math.BigDecimal;
import java.util.UUID;

public interface SearchDistanceProjection {
  UUID getId();

  UUID getProducerId();

  UUID getLocationId();

  BigDecimal getDistance();
}
