package com.green.yp.producer.data.repository;

import com.green.yp.producer.data.model.Producer;
import com.green.yp.producer.data.record.ProducerLocationDistanceProjection;
import com.green.yp.producer.data.record.ProducerSearchRecord;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProducerSearchRepository extends JpaRepository<Producer, UUID> {

  @Query(value = """
    SELECT 
      p.id as producerId,
      pl.id as locationId,
      p.name as producerName,
      ST_Distance_Sphere(pl.location_geo_point, ST_GeomFromText( ?1, 4326)) AS distance
    FROM producer p
    JOIN producer_location pl ON p.id = pl.producer_id
    WHERE pl.active = true
      AND ( ?3 IS NULL OR EXISTS (SELECT 1 FROM producer_line_of_business plob WHERE plob.producer_id = p.id AND plob.line_of_business_id = ?3
      ))
      AND (?2 IS NULL OR ST_Distance_Sphere(pl.location_geo_point, ST_GeomFromText(?1, 4326)) <= ?2
      )
    ORDER BY distance ASC, p.name ASC
    """,
          countQuery = """
    SELECT COUNT(*)
    FROM producer p
    JOIN producer_location pl ON p.id = pl.producer_id
    WHERE pl.active = true
      AND (?3 IS NULL OR EXISTS ( SELECT 1 FROM producer_line_of_business plob WHERE plob.producer_id = p.id AND plob.line_of_business_id = ?3
      ))
      AND (?2 IS NULL OR ST_Distance_Sphere(pl.location_geo_point, ST_GeomFromText(?1, 4326)) <= ?2
      )
    """,
          nativeQuery = true)
  Page<ProducerLocationDistanceProjection> findProducersWithinDistance(
          @Param("wktPoint") String wktPoint,
          @Param("distanceFilter") Integer distanceFilter,
          @Param("categoryId") UUID categoryId,
          Pageable pageable);



  @Query(
      """
        SELECT new com.green.yp.producer.data.record.ProducerSearchRecord(
            producer, location, contact,
            CAST(
                CASE
                    WHEN :distance IS NOT NULL THEN
                        ROUND(
                            3959.0 * acos(cos(radians(:latitude)) * cos(radians(location.latitude)) *
                            cos(radians(location.longitude) - radians(:longitude)) +
                            sin(radians(:latitude)) * sin(radians(location.latitude))), 2)
                    ELSE 0.0
            END AS java.math.BigDecimal)
    )
    FROM Producer producer
    JOIN ProducerLocation location ON producer.id = location.producerId
    JOIN ProducerContact contact ON contact.producerLocationId = location.id
    WHERE location.id IN :producerLocationIds
    AND contact.displayContactType != com.green.yp.api.apitype.producer.enumeration.ProducerDisplayContactType.NO_DISPLAY
    AND contact.producerContactType = com.green.yp.api.apitype.producer.enumeration.ProducerContactType.PRIMARY
    ORDER BY
        (3959.0 * acos(cos(radians(:latitude)) * cos(radians(location.latitude)) *
        cos(radians(location.longitude) - radians(:longitude)) +
        sin(radians(:latitude)) * sin(radians(location.latitude)))) ASC,
        producer.name ASC
    """)
  List<ProducerSearchRecord> findProducers(@Param("producerLocationIds") List<UUID> locationIds,
                                           @Param("distance") Integer distance,
                                           @Param("latitude") Double latitude,
                                           @Param("longitude") Double longitude);

  @Query(value = """
    SELECT new com.green.yp.producer.data.record.ProducerSearchRecord(
        producer, location, contact, null)
    FROM Producer producer
    JOIN ProducerLocation location ON producer.id = location.producerId
    JOIN ProducerContact contact ON contact.producerLocationId = location.id
    WHERE location.active = true
    AND contact.displayContactType != com.green.yp.api.apitype.producer.enumeration.ProducerDisplayContactType.NO_DISPLAY
    AND contact.producerContactType = com.green.yp.api.apitype.producer.enumeration.ProducerContactType.PRIMARY
    AND EXISTS (
        SELECT 1 FROM ProducerLineOfBusiness plob
        WHERE plob.producer = producer
        AND plob.lineOfBusinessId = :lineOfBusinessId
    )
    ORDER BY producer.createDate DESC
    """)
  List<ProducerSearchRecord> findMostRecentProfiles(
          @Param("lineOfBusinessId") UUID lineOfBusinessId,
          Limit limit);
  
  @Query("""
    SELECT new com.green.yp.producer.data.record.ProducerSearchRecord(
        producer, location, contact, null)
    FROM Producer producer
    JOIN ProducerLocation location ON producer.id = location.producerId
    JOIN ProducerContact contact ON contact.producerLocationId = location.id
    WHERE location.id = :producerLocationId
    AND location.active = true
    AND contact.displayContactType != com.green.yp.api.apitype.producer.enumeration.ProducerDisplayContactType.NO_DISPLAY
    AND contact.producerContactType = com.green.yp.api.apitype.producer.enumeration.ProducerContactType.PRIMARY
    """)
  Optional<ProducerSearchRecord> findProducerProfile(@Param("producerLocationId") UUID producerLocationId);


}