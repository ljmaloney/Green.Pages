package com.green.yp.api.contract;

import com.green.yp.api.apitype.enumeration.AuditActionType;
import com.green.yp.api.apitype.enumeration.AuditObjectType;
import com.green.yp.audit.service.AuditService;
import org.springframework.stereotype.Service;

@Service
public class ProducerAuditContract {

  final AuditService auditService;

  public ProducerAuditContract(AuditService auditService) {
    this.auditService = auditService;
  }

  public void createAuditRecord(
      AuditObjectType objectType,
      AuditActionType actionType,
      String userId,
      String ipAddress,
      String clazzName,
      String jsonPayload) {
    auditService.createAuditRecord(
        objectType, actionType, userId, ipAddress, clazzName, jsonPayload);
  }
}
