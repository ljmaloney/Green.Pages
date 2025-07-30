package com.green.yp.producer.data.repository;

import com.green.yp.producer.data.model.ProducerSubscriptionProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface ProducerSubProcessRepository extends JpaRepository<ProducerSubscriptionProcess, UUID> {

  @Query(
      value = """
        SELECT producer.id
        FROM
          Producer producer
          INNER JOIN ProducerSubscription subs on subs.producerId = producer.id
        WHERE
          producer.subscriptionType = com.green.yp.api.apitype.producer.enumeration.ProducerSubscriptionType.LIVE_ACTIVE
          AND producer.cancelDate IS NULL AND subs.endDate IS NULL
          AND producer.lastBillDate <= :invoiceDate
    """)
  List<UUID> getProducersToProcess(@Param("invoiceDate") OffsetDateTime invoiceDate);

    void deleteByProducerId(UUID producer);
}
