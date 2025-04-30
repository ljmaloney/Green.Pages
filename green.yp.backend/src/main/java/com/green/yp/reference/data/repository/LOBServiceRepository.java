package com.green.yp.reference.data.repository;

import com.green.yp.reference.data.model.LOBService;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LOBServiceRepository extends JpaRepository<LOBService, UUID> {
    List<LOBService> findLOBServicesByLineOfBusinessId(UUID lineOfBusinessId);
}
