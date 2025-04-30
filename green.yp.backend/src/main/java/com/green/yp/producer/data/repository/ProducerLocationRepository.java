package com.green.yp.producer.data.repository;

import com.green.yp.api.apitype.producer.enumeration.ProducerLocationType;
import com.green.yp.producer.data.model.ProducerLocation;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProducerLocationRepository extends JpaRepository<ProducerLocation, UUID> {
  @Query(
      """
                        SELECT location
                        FROM    ProducerLocation location
                        WHERE location.producerId=:producerId
                              AND location.locationType=:locationType
                              AND location.active=:active
                    """)
  Optional<ProducerLocation> findLocation(
      @Param("producerId") UUID producerId,
      @Param("active") Boolean active,
      @Param("locationType") ProducerLocationType locationType);

  List<ProducerLocation> findProducerLocationsByProducerId(@NotNull @NonNull UUID producerId);

  @Query(
      """
                        SELECT location
                        FROM    ProducerLocation location
                        WHERE location.producerId=:producerId
                              AND location.active=:active
                    """)
  List<ProducerLocation> findActiveLocations(
      @Param("producerId") UUID producerId, @Param("active") Boolean active);

  @Modifying
  @Query(
      """
        DELETE FROM ProducerLocation WHERE producerId in (:producerIds)
    """)
  void deleteLocations(@NotNull @NonNull @Param("producerIds") List<UUID> producerIds);
}
