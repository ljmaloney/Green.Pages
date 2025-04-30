package com.green.yp.producer.data.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.UncheckedIOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

@Slf4j
@Converter(autoApply = true)
public class ProductAttributesConverter implements AttributeConverter<Map<String, Object>, String> {
  @Override
  public String convertToDatabaseColumn(Map<String, Object> attributeMap) {
    if (CollectionUtils.sizeIsEmpty(attributeMap)) {
      try {
        return new ObjectMapper().writeValueAsString(attributeMap);
      } catch (JsonProcessingException e) {
        log.warn("Unexpected error converting attributeMap to JSON: {}", e.getMessage(), e);
        throw new RuntimeException(e);
      }
    }
    return null;
  }

  @Override
  public Map<String, Object> convertToEntityAttribute(String dbData) {
    if (StringUtils.isBlank(dbData)) {
      return Map.of();
    }
    try {
      ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
      return mapper.readValue(dbData, new TypeReference<>() {});
    } catch (JsonProcessingException e) {
      log.warn("Unexpected error converting JSON to map: {}", e.getMessage(), e);
      throw new UncheckedIOException(e);
    }
  }
}
