package com.green.yp.payment.data.model;

import com.green.yp.common.data.embedded.Immutable;
import com.green.yp.payment.data.enumeration.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
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
@Table(name = "producer_payment_transaction", schema = "greenyp")
public class ProducerPaymentTransaction extends Immutable {

  @NotNull
  @NonNull
  @Column(name = "producer_id", nullable = false, updatable = false)
  private UUID producerId;

  @NotNull
  @NonNull
  @Column(name = "payment_method_id", nullable = false, updatable = false)
  private UUID paymentMethodId;

  @NotNull
  @NonNull
  @Column(name = "producer_invoice_id", nullable = false, updatable = false)
  private UUID invoiceId;

  @NotNull
  @NonNull
  @Column(name = "payment_type", nullable = false, updatable = false)
  @Enumerated(EnumType.STRING)
  private ProducerPaymentType paymentType;

  @NotNull
  @NonNull
  @Column(name = "amount", nullable = false, updatable = false)
  private BigDecimal amount;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private PaymentTransactionStatus status;

  @Column(name = "reference_number", updatable = false)
  private String acquirerReferenceNumber;

  @Convert(converter = AvsErrorResponseCode.AvsErrorResponseCodeConverter.class)
  @Column(name = "avs_error_code", length = 1, updatable = false)
  private AvsErrorResponseCode avsErrorResponseCode;

  @Convert(converter = AvsResponseCode.AvsResponseCodeConverter.class)
  @Column(name = "avs_postal_response_code", length = 1, updatable = false)
  private AvsResponseCode avsPostalCodeResponseCode;

  @Convert(converter = AvsResponseCode.AvsResponseCodeConverter.class)
  @Column(name = "avs_street_addr_response_code", length = 1, updatable = false)
  private AvsResponseCode avsStreetAddressResponseCode;

  @Convert(converter = CvvResponseCode.CvvResponseCodeConverter.class)
  @Column(name = "cvv_response_code", length = 1, updatable = false)
  private CvvResponseCode cvvResponseCode;

  @Column private String responseCode;

  @Column private String responseText;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    ProducerPaymentTransaction that = (ProducerPaymentTransaction) o;

    return new EqualsBuilder().appendSuper(super.equals(o)).append(producerId, that.producerId).append(paymentMethodId, that.paymentMethodId).append(invoiceId, that.invoiceId).append(paymentType, that.paymentType).append(amount, that.amount).append(status, that.status).append(acquirerReferenceNumber, that.acquirerReferenceNumber).append(avsErrorResponseCode, that.avsErrorResponseCode).append(avsPostalCodeResponseCode, that.avsPostalCodeResponseCode).append(avsStreetAddressResponseCode, that.avsStreetAddressResponseCode).append(cvvResponseCode, that.cvvResponseCode).append(responseCode, that.responseCode).append(responseText, that.responseText).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(producerId).append(paymentMethodId).append(invoiceId).append(paymentType).append(amount).append(status).append(acquirerReferenceNumber).append(avsErrorResponseCode).append(avsPostalCodeResponseCode).append(avsStreetAddressResponseCode).append(cvvResponseCode).append(responseCode).append(responseText).toHashCode();
  }

}
