package com.green.yp.search.data.entity;

import java.math.BigDecimal;

public record SearchRecord(SearchMaster searchMaster,
                           BigDecimal distanceMiles) {}
