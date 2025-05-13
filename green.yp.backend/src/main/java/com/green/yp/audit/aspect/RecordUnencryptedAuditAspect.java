package com.green.yp.audit.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.yp.api.AuditRequest;
import com.green.yp.api.contract.ProducerAuditContract;
import com.green.yp.exception.SystemException;
import com.green.yp.util.RequestUtil;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class RecordUnencryptedAuditAspect {

    private final ProducerAuditContract auditContract;

    public RecordUnencryptedAuditAspect(ProducerAuditContract auditContract) {
        this.auditContract = auditContract;
    }

    @Pointcut(value = "@annotation(com.green.yp.api.AuditRequest)")
    void methodAnnotatedWithAuditRequest() {
        log.info("method annotated with audit request");
    }

    @Before(value = "methodAnnotatedWithAuditRequest()")
    public void before(JoinPoint joinPoint) {
        log.info("before calling method annotated with audit request");
    }

    @AfterReturning(pointcut = "methodAnnotatedWithAuditRequest()")
    public void writeAuditLog(JoinPoint joinPoint) {
        log.info("called method annotated with audit request");

        Object[] args = joinPoint.getArgs();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();

        Method method = signature.getMethod();
        AuditRequest annotation = method.getAnnotation(AuditRequest.class);

        String requestParameterName = annotation.requestParameter();

        Object requestPayload = getParameter(requestParameterName, paramNames, args);
        String userId = (String) getParameter("userId", paramNames, args);
        String ipAddress = (String) getParameter("ipAddress", paramNames, args);
        if (ipAddress == null) {
            ipAddress = RequestUtil.getRequestIP();
        }

        try {
            auditContract.createAuditRecord(
                    annotation.objectType(),
                    annotation.actionType(),
                    StringUtils.isNotBlank(userId) ? userId : ipAddress,
                    ipAddress,
                    requestPayload != null ? requestPayload.getClass().getSimpleName() : "",
                    requestPayload != null ? new ObjectMapper().writeValueAsString(requestPayload) : null );
        } catch (JsonProcessingException e) {
            log.error("Unexpected error serializing payload for audit contract");
            throw new SystemException("Unexpected error serializing payload for audit contract", e);
        }
    }

    private Object getParameter(String parameterName, String[] names, Object[] values) {
         for (int i = 0; i < names.length; i++) {
            if (parameterName.equals(names[i])) {
                return values[i];
            }
        }
        return null;
    }
}
