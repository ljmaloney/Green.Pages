package com.green.yp.classifieds.data.repository;

import com.green.yp.classifieds.data.model.ClassifiedAdType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassifiedAdTypeRepository extends JpaRepository<ClassifiedAdType, UUID> {
  List<ClassifiedAdType> findClassifiedAdTypeByActive(Boolean aTrue);
}
