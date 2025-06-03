package com.green.yp.producer.data.record;

import com.green.yp.producer.data.model.Producer;
import com.green.yp.producer.data.model.ProducerContact;
import com.green.yp.producer.data.model.ProducerLocation;
import java.math.BigDecimal;

public record ProducerSearchRecord(
    Producer producer, ProducerLocation location, ProducerContact contact, BigDecimal distance) {}
