package com.green.yp.classifieds.data.repository;

import com.green.yp.classifieds.data.model.Classified;
import com.green.yp.classifieds.data.model.ClassifiedCustomerProjection;
import com.green.yp.classifieds.data.model.ClassifiedSearchProjection;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Limit;
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

  @Query("""
    SELECT new com.green.yp.classifieds.data.model.ClassifiedSearchProjection(classified, adType, category, null)
    FROM Classified AS classified
        INNER JOIN ClassifiedAdType AS adType on adType.id = classified.adTypeId
        INNER JOIN ClassifiedCategory AS category on category.id = classified.categoryId
    WHERE
       (:classifiedId IS NULL OR classified.id=:classifiedId)
""")
  List<ClassifiedSearchProjection> getMostRecent(@NotNull @Param("classifiedId") UUID categoryId, Limit limit);

  @Query("""
        SELECT classified
        FROM Classified classified
        WHERE classified.activeDate IS NULL AND classified.lastActiveDate IS NULL
            AND classified.createDate <= :olderThan
    """)
  List<Classified> findUnpaidAds(@Param("olderThan") OffsetDateTime olderThan);
}
