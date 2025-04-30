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
 * Enumeration of supported AVS error response codes, codes listed on
 * https://developer.paypal.com/braintree/docs/reference/response/transaction#avs_error_response_code
 *
 * @author luther.maloney
 */
@Getter
@AllArgsConstructor
public enum AvsErrorResponseCode {
  NOT_SUPPORTED("S", "Not Supported"),
  AVS_ERROR("E", "AVS System Error");

  private static final Map<String, AvsErrorResponseCode> ERROR_CODE_MAP =
      Stream.of(values())
          .collect(Collectors.toMap(AvsErrorResponseCode::getErrorCode, Function.identity()));

  private final String errorCode;
  private final String description;

  public static AvsErrorResponseCode fromErrorCode(String errorCode) {
    if (StringUtils.isBlank(errorCode)) {
      return null;
    }

    return ERROR_CODE_MAP.computeIfAbsent(
        errorCode,
        key -> {
          throw new IllegalArgumentException(key);
        });
  }

  @Converter(autoApply = true)
  public static class AvsErrorResponseCodeConverter
      implements AttributeConverter<AvsErrorResponseCode, String> {

    @Override
    public String convertToDatabaseColumn(AvsErrorResponseCode type) {
      if (type == null) {
        return null;
      }
      return type.getErrorCode();
    }

    @Override
    public AvsErrorResponseCode convertToEntityAttribute(String code) {
      return AvsErrorResponseCode.fromErrorCode(code);
    }
  }
}
