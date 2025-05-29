package com.green.yp.producer.data.repository;

import com.green.yp.producer.data.model.ProducerLineOfBusiness;
import com.green.yp.producer.data.model.ProducerLineOfBusinessId;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProducerLobRepository
    extends JpaRepository<ProducerLineOfBusiness, ProducerLineOfBusinessId> {
  @Modifying
  @Query(
      """
                DELETE FROM ProducerLineOfBusiness pl WHERE pl.producerId IN (:producerIds)
            """)
  int deleteProducerLinesOfBusiness(@NotNull @NonNull @Param("producerIds") List<UUID> producerIds);
}
