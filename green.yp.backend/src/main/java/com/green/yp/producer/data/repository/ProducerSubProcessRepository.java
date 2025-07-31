package com.green.yp.producer.data.repository;

import com.green.yp.api.apitype.enumeration.ProducerSubProcessType;
import com.green.yp.producer.data.model.ProducerSubscriptionProcess;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
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

  @Modifying
  @Query("DELETE FROM ProducerSubscriptionProcess where producerId=:producerId")
  void deleteByProducerId(@Param("producerId") UUID producer);

  @Query(
      nativeQuery = true,
      value = """
           SELECT *
           FROM producer_subscription_process
           WHERE process_step='PREPARE'
           LIMIT ?1
           FOR UPDATE SKIP LOCKED
        """)
  List<ProducerSubscriptionProcess> findItemsToProcess(
      @Param("maxNumberToProcess") int maxNumberToProcess);

  @Modifying
  @Query("""
        UPDATE ProducerSubscriptionProcess SET processStep = :processStep WHERE producerId in (:producerIds)
""")
  void updateStatus(@Param("producerIds") List<UUID> producerIds,
                    @Param("processStep") ProducerSubProcessType producerSubProcessType);

  Optional<ProducerSubscriptionProcess> findByProducerId(@NonNull @NotNull UUID producerId);
}
