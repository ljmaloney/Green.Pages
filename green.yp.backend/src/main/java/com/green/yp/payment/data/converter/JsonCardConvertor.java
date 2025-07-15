package com.green.yp.payment.data.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.yp.exception.SystemException;
import com.green.yp.payment.data.json.Card;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class JsonCardConvertor implements AttributeConverter<Card, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public String convertToDatabaseColumn(Card card) {
        try {
            return card != null ? objectMapper.writeValueAsString(card) : null;
        } catch (Exception e) {
            log.error("Unexpected error converting map to JSON string", e);
            throw new SystemException("Error converting map to JSON", e);
        }
    }

    @Override
    public Card convertToEntityAttribute(String dbData) {
        try {
            return dbData != null ? objectMapper.readValue(dbData, Card.class) : null;
        } catch (Exception e) {
            log.error("Unexpected error converting JSON string to map", e);
            throw new SystemException("Error converting JSON to map", e);
        }
    }
}
