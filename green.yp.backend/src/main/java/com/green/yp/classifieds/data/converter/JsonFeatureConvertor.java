package com.green.yp.classifieds.data.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.yp.classifieds.data.model.ClassifiedAdFeature;
import com.green.yp.exception.SystemException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class JsonFeatureConvertor implements AttributeConverter<ClassifiedAdFeature, String> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(ClassifiedAdFeature attribute) {
    try {
      return attribute != null ? objectMapper.writeValueAsString(attribute) : null;
    } catch (Exception e) {
      log.error("Unexpected error converting map to JSON string", e);
      throw new SystemException("Error converting map to JSON", e);
    }
  }

  @Override
  public ClassifiedAdFeature convertToEntityAttribute(String dbData) {
    try {
      return dbData != null ? objectMapper.readValue(dbData, ClassifiedAdFeature.class) : null;
    } catch (Exception e) {
      log.error("Unexpected error converting JSON string to map", e);
      throw new SystemException("Error converting JSON to map", e);
    }
  }
}
