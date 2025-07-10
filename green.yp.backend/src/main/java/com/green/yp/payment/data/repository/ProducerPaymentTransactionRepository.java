package com.green.yp.payment.data.repository;

import com.green.yp.payment.data.enumeration.PaymentTransactionStatus;
import com.green.yp.payment.data.enumeration.ProducerPaymentType;
import com.green.yp.payment.data.model.ProducerPaymentTransaction;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProducerPaymentTransactionRepository
    extends JpaRepository<ProducerPaymentTransaction, UUID> {
  @Query(
      """
                      SELECT trans
                      FROM ProducerPaymentTransaction trans
                      WHERE
                          trans.invoiceId=:invoiceId
                          AND trans.paymentType=:paymentType
                          AND trans.status=:status
                    """)
  Optional<ProducerPaymentTransaction> findTransaction(
      @Param("invoiceId") UUID invoiceId,
      @Param("paymentType") ProducerPaymentType paymentType,
      @Param("status") PaymentTransactionStatus status);
}
