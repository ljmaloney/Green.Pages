package com.green.yp.classifieds.data.repository;

import com.green.yp.classifieds.data.model.Classified;
import com.green.yp.classifieds.data.model.ClassifiedCustomerProjection;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClassifiedRepository extends JpaRepository<Classified, UUID> {
  @Query(
      """
            SELECT new com.green.yp.classifieds.data.model.ClassifiedCustomerProjection(classified, customer)
            FROM Classified AS classified
                 INNER JOIN ClassifiedCustomer AS customer ON customer.id = classified.classifiedCustomerId
            WHERE
                 classified.id = :classifiedId
         """)
  Optional<ClassifiedCustomerProjection> findClassifiedAndCustomer(
      @NotNull @Param("classifiedId") UUID classifiedId);
}
