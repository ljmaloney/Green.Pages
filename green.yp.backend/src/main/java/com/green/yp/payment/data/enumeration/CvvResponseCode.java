package com.green.yp.payment.data.enumeration;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * Enumeration of supported AVS response codes, codes listed on
 * https://developer.paypal.com/braintree/docs/reference/response/transaction#cvv_response_code
 */
@Getter
@AllArgsConstructor
public enum CvvResponseCode {
    MATCHES("M", "Matches"),
    DOES_NOT_MATCH("N", "Does not match"),
    NOT_VERIFIED("U", "Not verified"),
    NOT_PROVIDED("I", "Not provided"),
    NOT_APPLICABLE("A", "Not Applicable"),
    BYPASS("B", "Bypass");

    private static final Map<String, CvvResponseCode> RESPONSE_CODE_MAP =
            Stream.of(values())
                    .collect(Collectors.toMap(CvvResponseCode::getResponseCode, Function.identity()));

    private final String responseCode;
    private final String description;

    public static CvvResponseCode fromResponseCode(String responseCode) {
        if (StringUtils.isBlank(responseCode)) {
            return null;
        }

        return RESPONSE_CODE_MAP.computeIfAbsent(
                responseCode,
                key -> {
                    throw new IllegalArgumentException(key);
                });
    }

    @Override
    public String toString() {
        return responseCode;
    }

    @Converter(autoApply = true)
    public static class CvvResponseCodeConverter
            implements AttributeConverter<CvvResponseCode, String> {

        @Override
        public String convertToDatabaseColumn(CvvResponseCode type) {
            if (type == null) {
                return null;
            }
            return type.getResponseCode();
        }

        @Override
        public CvvResponseCode convertToEntityAttribute(String code) {
            return CvvResponseCode.fromResponseCode(code);
        }
    }
}
