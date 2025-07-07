package com.green.yp.payment.data.repository;

import com.green.yp.payment.data.model.ProducerPaymentMethod;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProducerPaymentMethodRepository extends JpaRepository<ProducerPaymentMethod, UUID> {

  @Query(
      """
                       SELECT
                            pm
                       FROM ProducerPaymentMethod pm
                       WHERE pm.producerId=:producerId and pm.cancelDate IS NULL
                            and pm.active=TRUE
                    """)
  Optional<ProducerPaymentMethod> findActiveMethod(@NotNull @NonNull @Param("producerId") UUID producerId);
}
