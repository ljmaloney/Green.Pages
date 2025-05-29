package com.green.yp.producer.data.repository;

import com.green.yp.api.apitype.producer.enumeration.ProducerContactType;
import com.green.yp.producer.data.model.ProducerContact;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProducerContactRepository extends JpaRepository<ProducerContact, UUID> {
  List<ProducerContact> findByProducerIdAndProducerLocationId(UUID producerId, UUID locationId);

  @Query(
      """
                        UPDATE ProducerContact
                        SET authenticationCancelDate=:cancelDate, version=version+1, lastUpdateDate=:lastUpdateDate
                        WHERE producerId=:producerId
                    """)
  int cancelProducerContact(
      @NotNull @NonNull @Param("producerId") UUID producerId,
      @NotNull @NonNull @Param("lastUpdateDate") OffsetDateTime lastUpdateDate,
      @NotNull @NonNull @Param("cancelDate") OffsetDateTime cancelDate);

  @Query(
      """
                       SELECT pc
                       FROM ProducerContact pc
                            INNER JOIN ProducerLocation loc
                                ON loc.id = pc.producerLocationId
                                   AND loc.producerId=:producerId
                                   AND loc.locationType=com.green.yp.api.apitype.producer.enumeration.ProducerLocationType.HOME_OFFICE_PRIMARY
                       WHERE
                            pc.producerId=:producerId AND pc.producerContactType IN (:types)
                    """)
  List<ProducerContact> findProducerContacts(
      @NotNull @NonNull @Param("producerId") UUID producerId,
      @NotNull @NonNull @Param("types") ProducerContactType... types);

  @Query(
      """
                    SELECT pc
                    FROM ProducerContact pc
                    WHERE
                      pc.producerId=:producerId
                      AND (:locationId IS NULL OR pc.producerLocationId=:locationId )
                      AND pc.producerContactType IN :types
                        """)
  List<ProducerContact> findProducerContacts(
      @NotNull @NonNull @Param("producerId") UUID producerId,
      @NotNull @NonNull @Param("locationId") UUID locationId,
      @NotNull @NonNull @Param("types") List<ProducerContactType> types);

  @Modifying
  @Query(
      """
        DELETE from ProducerContact
        WHERE producerId in (:producerIds)
    """)
  void deleteContacts(@NotNull @NonNull @Param("producerIds") List<UUID> producerIds);
}
