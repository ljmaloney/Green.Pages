package com.green.yp.invoice.data.repository;

import com.green.yp.api.apitype.invoice.InvoiceResponse;
import com.green.yp.api.apitype.invoice.InvoiceType;
import com.green.yp.invoice.data.model.Invoice;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.validator.constraints.ParameterScriptAssert;
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

  @Query("""
            SELECT invoice
            FROM Invoice invoice
            WHERE
                invoice.invoiceType = :invoiceType
                AND (:referenceId IS NULL OR invoice.externalRef = :referenceId)
                AND invoice.paidDate BETWEEN :startDate AND :endDate
        """)
  List<InvoiceResponse> findInvoices(
      @Param("invoiceType") InvoiceType invoiceType,
      @Param("referenceId") String referenceId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);
}
