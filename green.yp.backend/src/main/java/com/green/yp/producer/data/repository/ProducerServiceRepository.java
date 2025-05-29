package com.green.yp.producer.data.repository;

import com.green.yp.producer.data.model.ProducerService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProducerServiceRepository extends JpaRepository<ProducerService, UUID> {

  @Query(
      """
                        SELECT service
                        FROM  ProducerService service
                        WHERE service.producerId=:producerId
                            AND service.producerLocationId=:producerLocationId
                            AND service.shortDescription=:description

                    """)
  Optional<ProducerService> findProducerService(
      @Param("producerId") UUID producerId,
      @Param("producerLocationId") UUID producerLocationId,
      @Param("description") String description);

  @Query(
      """
                         SELECT service
                         FROM  ProducerService service
                         WHERE service.producerId=:producerId
                             AND service.producerLocationId=:producerLocationId
                    """)
  List<ProducerService> findServices(
      @Param("producerId") UUID producerId, @Param("producerLocationId") UUID producerLocationId);
}
