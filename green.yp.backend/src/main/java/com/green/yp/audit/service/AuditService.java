package com.green.yp.audit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.yp.api.apitype.enumeration.AuditActionType;
import com.green.yp.api.apitype.enumeration.AuditObjectType;
import com.green.yp.audit.data.model.ProducerAudit;
import com.green.yp.audit.data.repository.ProducerAuditRepository;
import com.green.yp.exception.SystemException;
import com.green.yp.util.RequestUtil;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuditService {

    private final ProducerAuditRepository repository;

    public AuditService(ProducerAuditRepository repository) {
        this.repository = repository;
    }

    @Async(value = "threadPoolAuditTaskExecutor")
    public void createAuditRecord(
            AuditObjectType objectType,
            AuditActionType actionType,
            String userId,
            String ipAddress,
            String clazzName,
            String jsonPayload) {
        log.info("Creating audit record for {} {} - ipAddress- {}", actionType, objectType, ipAddress);

        ProducerAudit audit =
                ProducerAudit.builder()
                        .ipAddress(ipAddress)
                        .objectType(objectType)
                        .actionType(actionType)
                        .className(clazzName)
                        .actionRefId(userId)
                        .objectData(jsonPayload)
                        .build();

        repository.saveAndFlush(audit);
    }

    @Async(value = "threadPoolAuditTaskExecutor")
    public void createBadAuthenticationAttempt(
            String userId, String password, String ipAddress, Integer badAttempts) {

        Map<String, String> badAttemptsMap = new HashMap<>();
        badAttemptsMap.put("userId", userId);
        badAttemptsMap.put("password", password);
        badAttemptsMap.put("ipAddress", ipAddress);
        badAttemptsMap.put("badAttempts", String.valueOf(badAttempts));

        try {
            ProducerAudit audit =
                    ProducerAudit.builder()
                            .ipAddress(RequestUtil.getRequestIP())
                            .objectType(AuditObjectType.BAD_AUTH_ATTEMPT)
                            .actionType(AuditActionType.BAD_AUTH_ATTEMPT)
                            .actionRefId(userId)
                            .className(badAttemptsMap.getClass().getSimpleName())
                            .objectData(new ObjectMapper().writeValueAsString(badAttemptsMap))
                            .build();
            repository.saveAndFlush(audit);
        } catch (JsonProcessingException e) {
            log.error("Unexpected error serializing payload for audit contract");
            throw new SystemException("Unexpected error serializing payload for audit contract", e);
        }
    }
}
