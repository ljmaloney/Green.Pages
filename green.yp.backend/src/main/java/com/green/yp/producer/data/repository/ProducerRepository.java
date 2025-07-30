package com.green.yp.producer.data.repository;

import com.green.yp.api.apitype.producer.enumeration.ProducerSubscriptionType;
import com.green.yp.producer.data.model.Producer;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProducerRepository extends JpaRepository<Producer, UUID> {
  @Query(
      """
                    Select producer
                    From Producer producer
                    Where producer.websiteUrl like %:hostname%
                  """)
  List<Producer> findByHostname(@NotNull @NonNull @Param("hostname") String hostname);

  @Query(
"""
        Select producer
        From Producer producer
        WHERE producer.lastUpdateDate < :lastUpdateDate
                AND producer.subscriptionType = :subscriptionType
""")
  List<Producer> findLastModified(
      @Param("lastUpdateDate") OffsetDateTime daysOld,
      @Param("subscriptionType") ProducerSubscriptionType producerSubscriptionType);

  @Modifying
  @Query(
      """
        DELETE FROM Producer p WHERE p.id in (:producerIds) AND p.subscriptionType=:producerSubscriptionType
    """)
  int delete(
      @NotNull @NonNull @Param("producerIds") List<UUID> producerIds,
      @NotNull @NonNull @Param("producerSubscriptionType")
          ProducerSubscriptionType producerSubscriptionType);

  List<Producer> findByLastUpdateDateBeforeAndSubscriptionType(OffsetDateTime offsetDateTime,
                                                               ProducerSubscriptionType producerSubscriptionType,
                                                               Limit of);
}
