package com.green.yp.common.data.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter(autoApply = true)
public class BooleanConverter implements AttributeConverter<Boolean, String> {
  @Override
  public String convertToDatabaseColumn(Boolean attribute) {
    return attribute == null || !attribute ? "N" : "Y";
  }

  @Override
  public Boolean convertToEntityAttribute(String dbData) {
    return "Y".equalsIgnoreCase(dbData);
  }
}
