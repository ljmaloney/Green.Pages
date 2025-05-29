package com.green.yp.producer.data.repository;

import com.green.yp.producer.data.model.ProducerSubscription;
import com.green.yp.producer.data.record.ProducerSubscriptionRecord;
import com.green.yp.reference.data.enumeration.SubscriptionType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProducerSubscriptionRepository extends JpaRepository<ProducerSubscription, UUID> {
  @Query(
      """
                        SELECT
                          new com.green.yp.producer.data.record.ProducerSubscriptionRecord(ps, subs)
                        FROM
                           ProducerSubscription ps
                           INNER JOIN Subscription subs on subs.id=ps.subscriptionId
                        WHERE
                           ps.producerId=:producerId
                    """)
  List<ProducerSubscriptionRecord> findAllSubscriptions(
      @NotNull @NonNull @Param("producerId") UUID producerId);

  @Query(
      """
                        SELECT
                          new com.green.yp.producer.data.record.ProducerSubscriptionRecord(ps, subs)
                        FROM
                           ProducerSubscription ps
                           INNER JOIN Subscription subs on subs.id=ps.subscriptionId
                        WHERE
                           ps.producerId=:producerId
                           AND (ps.endDate IS NULL OR ps.endDate > :currentDate)
                           AND subs.subscriptionType IN (:subscriptionTypes)
                        ORDER BY ps.createDate, ps.endDate
                    """)
  List<ProducerSubscriptionRecord> findActiveSubscriptions(
      @NotNull @NonNull @Param("producerId") UUID producerId,
      @Param("currentDate") LocalDate currentDate,
      @Param("subscriptionTypes") SubscriptionType... subscriptionTypes);

  @Modifying
  @Query(
      """
        DELETE FROM ProducerSubscription ps  WHERE ps.producerId IN (:producerIds)
    """)
  void deleteProducerSubscriptions(@NotNull @NonNull @Param("producerIds") List<UUID> producerIds);
}
