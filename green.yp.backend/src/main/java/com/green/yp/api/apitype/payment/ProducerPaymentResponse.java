package com.green.yp.api.apitype.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.green.yp.payment.data.enumeration.AvsErrorResponseCode;
import com.green.yp.payment.data.enumeration.AvsResponseCode;
import com.green.yp.payment.data.enumeration.CvvResponseCode;
import com.green.yp.payment.data.enumeration.ProducerPaymentType;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.NonNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProducerPaymentResponse(
    @NotNull @NonNull UUID transactionId,
    @NotNull @NonNull UUID producerId,
    @NotNull @NonNull UUID paymentMethodId,
    @NotNull @NonNull UUID producerInvoiceId,
    @NotNull @NonNull OffsetDateTime createDate,
    @NotNull @NonNull UUID methodId,
    @NotNull @NonNull ProducerPaymentType paymentType,
    @NotNull @NonNull BigDecimal amount,
    @NotNull @NonNull String acquirerReferenceNumber,
    @NonNull @NotNull CvvResponseCode cvvResponseCode,
    AvsErrorResponseCode avsErrorResponseCode,
    AvsResponseCode avsPostalResponseCode,
    AvsResponseCode avsStreetAddressResponseCode,
    @NotNull @NonNull String responseCode,
    @NotNull @NonNull String responseText) {
  public Boolean isSuccess() {
    return true;
  }
}
