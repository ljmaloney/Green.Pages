package com.green.yp.audit.data.repository;

import com.green.yp.audit.data.model.ProducerAudit;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProducerAuditRepository extends JpaRepository<ProducerAudit, UUID> {}
