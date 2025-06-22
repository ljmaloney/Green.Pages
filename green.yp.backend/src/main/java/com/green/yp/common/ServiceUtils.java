package com.green.yp.common;

import com.green.yp.api.apitype.PatchRequest;
import com.green.yp.common.data.embedded.Mutable;
import com.green.yp.exception.BusinessException;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.mapstruct.ap.internal.util.Collections;

import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;

@Slf4j
public class ServiceUtils {
  public static void patchEntity(PatchRequest patchRequest, Mutable mutable) {
    Map<String, Object> properties = patchRequest.patchParameters();
    properties.keySet().stream()
        .filter(propertyName -> properties.get(propertyName) != null)
        .forEach(
            propertyName -> {
              try {
                PropertyUtils.setProperty(mutable, propertyName, properties.get(propertyName));
              } catch (Exception e) {
                throw new BusinessException(
                    "Unexpected error patching entity {}", e, mutable.getClass().getSimpleName());
              }
            });
  }

  public static void patchEntity(
      PatchRequest patchRequest,
      Mutable mutable,
      BiFunction<String, Object, Object> conversionFunction) {
    Map<String, Object> properties = patchRequest.patchParameters();
    properties.keySet().stream()
        .filter(propertyName -> properties.get(propertyName) != null)
        .forEach(
            propertyName -> {
              try {
                Object value = conversionFunction.apply(propertyName, properties.get(propertyName));
                PropertyUtils.setProperty(mutable, propertyName, value);
              } catch (Exception e) {
                throw new BusinessException(
                    "Unexpected error patching entity {}", e, mutable.getClass().getSimpleName());
              }
            });
  }
  
  public static <T, R extends Record> void updateFromRecord(T entity, R recordRequest, String...ignoreFields) {
    try {
      Set<String> ignoreSet = Collections.asSet(ignoreFields);
      Arrays.stream(recordRequest.getClass().getRecordComponents())
          .filter(component -> !ignoreSet.contains(component.getName()))
          .forEach(
              component -> copyField(entity, recordRequest, component));
    } catch (Exception e) {
      throw new BusinessException(
          "Unexpected error updating entity from record {}", e, entity.getClass().getSimpleName());
    }
  }

    private static <T, R extends Record> void copyField(T entity, R recordClass, RecordComponent component) {
        String fieldName = component.getName();
        Method accessor = component.getAccessor();
        try {
          Object value = accessor.invoke(recordClass);
          if (value != null) {
            PropertyUtils.setProperty(entity, fieldName, value);
          }
        } catch (NoSuchMethodException e) {
          log.warn("Field from record not found in entity");
        } catch (Exception e) {
          throw new BusinessException(
              "Unexpected error updating entity from record {}",
              e,
              entity.getClass().getSimpleName());
        }
    }


}
