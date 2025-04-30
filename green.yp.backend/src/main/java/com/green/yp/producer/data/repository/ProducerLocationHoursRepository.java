package com.green.yp.producer.data.repository;

import com.green.yp.api.apitype.common.enumeration.DayOfWeekType;
import com.green.yp.producer.data.model.ProducerLocationHours;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProducerLocationHoursRepository
    extends JpaRepository<ProducerLocationHours, UUID> {
  List<ProducerLocationHours> findAllByProducerLocationId(UUID producerLocationId);

  @Query(
      """
                        SELECT hours
                        FROM ProducerLocationHours hours
                        WHERE hours.producerLocationId=:locationId AND hours.dayOfWeek=:dayOfWeek
                    """)
  Optional<ProducerLocationHours> findLocationHours(
      @NotNull @NonNull @Param("locationId") UUID locationId,
      @NotNull @NonNull @Param("dayOfWeek") DayOfWeekType dayOfWeekType);

  @Modifying
  @Query(
      """
                        DELETE FROM ProducerLocationHours as hours WHERE hours.producerLocationId=:locationId
                    """)
  Integer deleteAll(@NotNull @NonNull @Param("locationId") UUID locationId);

  @Modifying
  @Query(
      """
                        DELETE FROM ProducerLocationHours as hours
                        WHERE hours.producerId IN :producerIds
                    """)
  void deleteAllByProducers(@NotNull @NonNull @Param("producerIds") List<UUID> producerIds);
}
