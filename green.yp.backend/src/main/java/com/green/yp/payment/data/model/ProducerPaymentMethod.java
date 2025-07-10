package com.green.yp.payment.data.model;

import com.green.yp.common.data.converter.BooleanConverter;
import com.green.yp.common.data.embedded.Mutable;
import com.green.yp.payment.data.converter.PaymentCryptoConverter;
import com.green.yp.payment.data.enumeration.PaymentMethodType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "producer_payment_method", schema = "greenyp")
public class ProducerPaymentMethod extends Mutable {

  @Column(name = "active", nullable = false)
  @Convert(converter = BooleanConverter.class)
  private Boolean active;

  @Column(name = "capture_method", nullable = false)
  @Convert(converter = BooleanConverter.class)
  private Boolean captureMethod;

  @Column(name = "cancel_date")
  @Temporal(TemporalType.TIMESTAMP)
  private OffsetDateTime cancelDate;

  @Column(name = "producer_id", nullable = false)
  private UUID producerId;

  @Column(name = "method_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private PaymentMethodType paymentType;

  @Column(name = "pan_last_four", nullable = false, length = 4, columnDefinition = "CHAR(4)")
  private String panLastFour;

  @Column(name = "payment_method")
  @Convert(converter = PaymentCryptoConverter.class)
  private String paymentMethod;

  @NonNull
  @NotNull
  @Column(name = "payor_name", nullable = false)
  @Convert(converter = PaymentCryptoConverter.class)
  private String payorName;

  @NotNull
  @NonNull
  @Column(name = "payor_address_1", nullable = false)
  @Convert(converter = PaymentCryptoConverter.class)
  private String payorAddress1;

  @Column(name = "payor_address_2", nullable = false)
  @Convert(converter = PaymentCryptoConverter.class)
  private String payorAddress2;

  @NotNull
  @NonNull
  @Column(name = "payor_city", nullable = false)
  @Convert(converter = PaymentCryptoConverter.class)
  private String payorCity;

  @NotNull
  @NonNull
  @Column(name = "payor_state", nullable = false)
  @Convert(converter = PaymentCryptoConverter.class)
  private String payorState;

  @NotNull
  @NonNull
  @Column(name = "payor_postal_code", nullable = false)
  @Convert(converter = PaymentCryptoConverter.class)
  private String payorPostalCode;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    ProducerPaymentMethod that = (ProducerPaymentMethod) o;

    return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(active, that.active)
        .append(captureMethod, that.captureMethod)
        .append(cancelDate, that.cancelDate)
        .append(producerId, that.producerId)
        .append(paymentType, that.paymentType)
        .append(panLastFour, that.panLastFour)
        .append(paymentMethod, that.paymentMethod)
        .append(payorName, that.payorName)
        .append(payorAddress1, that.payorAddress1)
        .append(payorAddress2, that.payorAddress2)
        .append(payorCity, that.payorCity)
        .append(payorState, that.payorState)
        .append(payorPostalCode, that.payorPostalCode)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .appendSuper(super.hashCode())
        .append(active)
        .append(captureMethod)
        .append(cancelDate)
        .append(producerId)
        .append(paymentType)
        .append(panLastFour)
        .append(paymentMethod)
        .append(payorName)
        .append(payorAddress1)
        .append(payorAddress2)
        .append(payorCity)
        .append(payorState)
        .append(payorPostalCode)
        .toHashCode();
  }
}
