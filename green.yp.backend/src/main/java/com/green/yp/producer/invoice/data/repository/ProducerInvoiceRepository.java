package com.green.yp.producer.invoice.data.repository;

import com.green.yp.producer.invoice.data.model.ProducerInvoice;
import com.green.yp.reference.data.enumeration.SubscriptionType;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProducerInvoiceRepository extends JpaRepository<ProducerInvoice, UUID> {
  @Query(
      """
                        SELECT producerInvoice
                        FROM
                           ProducerInvoice producerInvoice
                           INNER JOIN Subscription sub ON sub.id = producerInvoice.subscriptionId
                        WHERE
                            producerInvoice.producerId=:producerId
                            AND sub.subscriptionType IN (:types)
                            AND producerInvoice.paidDate IS NULL
                    """)
  Optional<ProducerInvoice> findUnpaidSubscriptionInvoice(
      @NotNull @NonNull @Param("producerId") UUID producerId,
      @NonNull @Param("types") SubscriptionType... types);

  @Query(
      nativeQuery = true,
      value =
          """
                               SELECT count(*)
                               FROM producer_invoice
                               WHERE create_date between :startDate and :endDate
                            """)
  Integer getCountByDate(
      @Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    List<ProducerInvoice> findByProducerIdAndCreateDateBetween(UUID producerId, OffsetDateTime startDate, OffsetDateTime endDate, Sort createDateSort);
}
