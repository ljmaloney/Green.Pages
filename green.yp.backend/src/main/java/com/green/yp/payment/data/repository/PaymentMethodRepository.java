package com.green.yp.payment.data.repository;

import com.green.yp.payment.data.enumeration.PaymentMethodStatusType;
import com.green.yp.payment.data.model.PaymentMethod;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {

  @Query("""
    SELECT method
    FROM PaymentMethod method
    WHERE method.referenceId=:referenceId and method.statusType IN (:statusTypes)
""")
  Optional<PaymentMethod> findActiveMethod(
          @Param("referenceId") String referenceId,
          @Param("statusTypes") List<PaymentMethodStatusType> statusTypes);

    Optional<PaymentMethod> findByReferenceIdAndStatusTypeEquals(String referenceId,
                                                                 PaymentMethodStatusType paymentMethodStatusType);

  void deletePaymentMethodByReferenceIdAndStatusTypeEquals(String referenceId, PaymentMethodStatusType paymentMethodStatusType);
}
