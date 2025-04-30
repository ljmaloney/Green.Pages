package com.green.yp.invoice.data.repository;

import com.green.yp.invoice.data.model.Invoice;
import com.green.yp.reference.data.enumeration.SubscriptionType;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
  @Query(
      """
                        SELECT invoice
                        FROM
                           Invoice invoice
                           INNER JOIN Subscription sub ON sub.id = invoice.subscriptionId
                        WHERE
                            invoice.producerId=:producerId
                            AND sub.subscriptionType IN (:types)
                            AND invoice.paidDate IS NULL
                    """)
  Optional<Invoice> findUnpaidSubscriptionInvoice(
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
}
