package com.green.yp.classifieds.data.repository;

import com.green.yp.classifieds.data.ClassifiedSearchDistanceProjection;
import com.green.yp.classifieds.data.model.Classified;
import com.green.yp.classifieds.data.model.ClassifiedCustomerProjection;
import com.green.yp.classifieds.data.model.ClassifiedSearchProjection;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
       (:categoryId IS NULL OR category.id=:categoryId)
       AND classified.lastActiveDate IS NOT NULL
       AND :currentDate BETWEEN classified.activeDate AND classified.lastActiveDate
     ORDER BY classified.createDate
""")
  List<ClassifiedSearchProjection> getMostRecent(@NotNull @Param("currentDate") LocalDate currentDate,
                                                 @Param("categoryId") UUID categoryId, Limit limit);


  @Query("""
        SELECT classified
        FROM Classified classified
        WHERE classified.activeDate IS NULL AND classified.lastActiveDate IS NULL
            AND classified.createDate <= :olderThan
    """)
  List<Classified> findUnpaidAds(@Param("olderThan") OffsetDateTime olderThan);

  @Query( value = """
            SELECT
                bin_to_uuid(c.id) as classifiedId,
                c.title,
                ST_Distance_Sphere(c.location_geo_point, ST_GeomFromText( ?1, 4326))/1609.34 AS distance
            FROM classified c
            WHERE
              ?3 BETWEEN c.active_date and c.last_active_date
              AND (?4 IS NULL OR c.classified_category_id = ?4)
              AND (?5 IS NULL OR MATCH(title) AGAINST(?5 IN NATURAL LANGUAGE MODE))
              AND (?2 IS NULL OR ST_Distance_Sphere(c.location_geo_point, ST_GeomFromText(?1, 4326)) <= ?2)
            ORDER BY distance ASC
        """,
      countQuery = """
            SELECT COUNT(*)
            FROM classified c
            WHERE
              ?3 BETWEEN c.active_date and c.last_active_date
              AND (?4 IS NULL OR c.classified_category_id = ?4)
              AND (?5 IS NULL OR MATCH(title) AGAINST(?5 IN NATURAL LANGUAGE MODE))
              AND (?2 IS NULL OR ST_Distance_Sphere(c.location_geo_point, ST_GeomFromText(?1, 4326)) <= ?2)
            """, nativeQuery = true)
  Page<ClassifiedSearchDistanceProjection> searchClassifieds(
          @Param("wktPoint")  String wktPoint,
          @Param("distanceMeters") BigDecimal distanceMeters,
          @Param("currentDate")  LocalDate currentDate,
          @Param("classifiedCategoryId") UUID classifiedCategoryId,
          @Param("keywords") String keywords,
          Pageable pageable);

  @Query(
          """
            SELECT new com.green.yp.classifieds.data.model.ClassifiedSearchProjection(
                classified, adType, category,
                CAST(ROUND((3959.0 * acos(cos(radians(:latitude)) * cos(radians(classified.latitude)) *
                            cos(radians(classified.longitude) - radians(:longitude)) +
                           sin(radians(:latitude)) * sin(radians(classified.latitude)))), 2)
                AS java.math.BigDecimal) )
            FROM Classified classified
                INNER JOIN ClassifiedCategory category ON category.id = classified.categoryId
                INNER JOIN ClassifiedAdType adType ON adType.id = classified.adTypeId
            WHERE classified.id in :classifiedIds
        ORDER BY
            (3959.0 * acos(cos(radians(:latitude)) * cos(radians(classified.latitude)) *
            cos(radians(classified.longitude) - radians(:longitude)) +
            sin(radians(:latitude)) * sin(radians(classified.latitude)))) ASC,
            classified.title ASC
        """)
  List<ClassifiedSearchProjection> getSearchRsults(@Param("classifiedIds") List<UUID> classifiedIds,
                                                   @Param("latitude") Double latitude,
                                                   @Param("longitude") Double longitude);
}
