package com.green.yp.producer.data.repository;

import com.green.yp.producer.data.model.ProducerProduct;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProducerProductRepository extends JpaRepository<ProducerProduct, UUID> {
  List<ProducerProduct> findAllByProducerLocationId(UUID producerLocationId);
}
