package com.green.yp.producer.data.repository;

import com.green.yp.producer.data.model.Producer;
import com.green.yp.producer.data.record.ProducerSearchRecord;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProducerSearchRepository extends JpaRepository<Producer, UUID> {

    @Query("""
    SELECT new com.green.yp.producer.data.record.ProducerSearchRecord(
        producer,
        location,
        contact,
        CAST(
            CASE 
                WHEN :distance IS NOT NULL THEN 
                    ROUND(
                        3959.0 * acos(cos(radians(:latitude)) * cos(radians(location.latitude)) *
                        cos(radians(location.longitude) - radians(:longitude)) +
                        sin(radians(:latitude)) * sin(radians(location.latitude)))
                    , 2)
                ELSE 0.0
            END AS java.math.BigDecimal)
    )
    FROM Producer producer
    JOIN ProducerLocation location ON producer.id = location.producerId
    JOIN ProducerContact contact ON contact.producerLocationId = location.id
    WHERE location.active = true
    AND contact.displayContactType != com.green.yp.api.apitype.producer.enumeration.ProducerDisplayContactType.NO_DISPLAY
    AND contact.producerContactType = com.green.yp.api.apitype.producer.enumeration.ProducerContactType.PRIMARY
    AND (:categoryId IS NULL OR EXISTS (
        SELECT 1 FROM ProducerLineOfBusiness plob 
        WHERE plob.producer = producer 
        AND plob.lineOfBusinessId = :categoryId
    ))
    AND (:distance IS NULL OR
        (3959.0 * acos(cos(radians(:latitude)) * cos(radians(location.latitude)) *
        cos(radians(location.longitude) - radians(:longitude)) +
        sin(radians(:latitude)) * sin(radians(location.latitude)))) <= :distance)
    ORDER BY 
        (3959.0 * acos(cos(radians(:latitude)) * cos(radians(location.latitude)) *
        cos(radians(location.longitude) - radians(:longitude)) +
        sin(radians(:latitude)) * sin(radians(location.latitude)))) ASC,
        producer.name ASC
    """)
    Page<ProducerSearchRecord> findProducers(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("distance") Integer distance,
            @Param("categoryId") UUID categoryId,
            @Param("serviceId") UUID serviceId,
            Pageable pageable);
}