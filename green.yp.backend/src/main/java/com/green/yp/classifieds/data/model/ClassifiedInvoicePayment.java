package com.green.yp.classifieds.data.model;

import com.green.yp.common.data.embedded.Immutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@Setter
@Entity
@Table(name = "classified_invoice_payment", schema = "greenyp")
public class ClassifiedInvoicePayment extends Immutable {

  @NotNull
  @Column(name = "classified_id", nullable = false)
  private UUID classifiedId;

  @NotNull
  @Column(name = "classified_ad_type", nullable = false)
  private UUID classifiedAdTypeId;

  @NotNull
  @Column(name = "classified_customer_id", nullable = false)
  private UUID classifiedCustomerId;

  @Size(max = 20)
  @NotNull
  @Column(name = "invoice_number", nullable = false, length = 20)
  private String invoiceNumber;

  @Size(max = 50)
  @NotNull
  @Column(name = "payment_customer_ref", nullable = false, length = 50)
  private String paymentCustomerRef;

  @Size(max = 50)
  @NotNull
  @Column(name = "payment_ref", nullable = false, length = 50)
  private String paymentRef;

  @NotNull
  @Column(name="payment_amount", nullable = false)
  private BigDecimal paymentAmount;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    ClassifiedInvoicePayment that = (ClassifiedInvoicePayment) o;

    return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(classifiedId, that.classifiedId)
        .append(classifiedAdTypeId, that.classifiedAdTypeId)
        .append(classifiedCustomerId, that.classifiedCustomerId)
        .append(invoiceNumber, that.invoiceNumber)
        .append(paymentCustomerRef, that.paymentCustomerRef)
        .append(paymentRef, that.paymentRef)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .appendSuper(super.hashCode())
        .append(classifiedId)
        .append(classifiedAdTypeId)
        .append(classifiedCustomerId)
        .append(invoiceNumber)
        .append(paymentCustomerRef)
        .append(paymentRef)
        .toHashCode();
  }
}
