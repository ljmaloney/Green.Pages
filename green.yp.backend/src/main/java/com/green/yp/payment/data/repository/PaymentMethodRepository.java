package com.green.yp.payment.data.repository;

import com.green.yp.payment.data.model.PaymentMethod;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {
    Optional<PaymentMethod> findPaymentMethodByReferenceIdAndActive(String referenceId, boolean active);
}
