package com.green.yp.api;

import com.green.yp.api.apitype.enumeration.AuditActionType;
import com.green.yp.api.apitype.enumeration.AuditObjectType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditRequest {
  String requestParameter();

  AuditObjectType objectType() default AuditObjectType.OBJECT;

  AuditActionType actionType() default AuditActionType.LOG_ACTION;
}
