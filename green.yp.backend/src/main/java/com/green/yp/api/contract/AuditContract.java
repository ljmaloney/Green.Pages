package com.green.yp.api.contract;

import com.green.yp.api.apitype.enumeration.AuditActionType;
import com.green.yp.api.apitype.enumeration.AuditObjectType;
import com.green.yp.audit.service.AuditService;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class AuditContract {
  private final AuditService auditService;

  public AuditContract(AuditService auditService) {
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

  public void createBadAuthenticationAttempt(
      @NotNull @NonNull String userId,
      @NotNull @NonNull String password,
      @NotNull @NonNull String ipAddress,
      @NotNull @NonNull Integer badAttempts) {
    auditService.createBadAuthenticationAttempt(userId, password, ipAddress, badAttempts);
  }
}
