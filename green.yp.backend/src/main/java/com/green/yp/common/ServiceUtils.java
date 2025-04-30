package com.green.yp.common;

import com.green.yp.api.apitype.PatchRequest;
import com.green.yp.common.data.embedded.Mutable;
import com.green.yp.exception.BusinessException;
import java.util.Map;
import java.util.function.BiFunction;
import org.apache.commons.beanutils.PropertyUtils;

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
}
