package com.green.yp.producer.data.record;

import com.green.yp.producer.data.model.Producer;
import com.green.yp.producer.data.model.ProducerContact;
import com.green.yp.producer.data.model.ProducerLocation;
import com.green.yp.reference.data.model.LineOfBusiness;

import java.math.BigDecimal;

public record ProducerSearchRecord(
        Producer producer, ProducerLocation location,
        ProducerContact contact, LineOfBusiness lineOfBusiness, BigDecimal distance) {}
