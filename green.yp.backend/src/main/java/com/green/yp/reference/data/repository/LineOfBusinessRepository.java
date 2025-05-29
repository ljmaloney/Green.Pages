package com.green.yp.reference.data.repository;

import com.green.yp.reference.data.model.LineOfBusiness;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineOfBusinessRepository extends JpaRepository<LineOfBusiness, UUID> {
  default List<LineOfBusiness> findAllOrderByLineOfBusinessAsc() {
    return findAll(Sort.by(Sort.Direction.ASC, "lineOfBusinessName"));
  }

  Optional<LineOfBusiness> findLineOfBusinessByLineOfBusinessName(String lineOfBusiness);
}
