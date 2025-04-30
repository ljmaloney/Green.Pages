package com.green.yp.producer.data.record;

import com.green.yp.producer.data.model.ProducerSubscription;
import com.green.yp.reference.data.model.Subscription;

public record ProducerSubscriptionRecord(
    ProducerSubscription producerSubscription, Subscription subscription) {}
