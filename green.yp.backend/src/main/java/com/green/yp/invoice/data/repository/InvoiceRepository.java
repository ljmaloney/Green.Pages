package com.green.yp.invoice.data.repository;

import com.green.yp.invoice.data.model.Invoice;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    @Query(
            nativeQuery = true,
            value =
                    """
                               SELECT count(*)
                               FROM invoice
                               WHERE create_date between :startDate and :endDate
                            """)
    Integer getCountByDate(
            @Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);
}
