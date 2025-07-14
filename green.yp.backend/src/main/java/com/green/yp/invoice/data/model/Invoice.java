package com.green.yp.invoice.data.model;

import com.green.yp.api.apitype.invoice.InvoiceType;
import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@Setter
@Entity
@Table(name = "invoice", schema = "greenyp")
public class Invoice extends Mutable {

  @Column(name = "payment_transaction_id")
  private UUID paymentTransactionId;

  @Size(max = 50)
  @NotNull
  @Column(name = "external_ref", nullable = false, length = 50)
  private String externalRef;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "invoice_type", nullable = false, length = 50)
  private InvoiceType invoiceType;

  @Column(name = "paid_date")
  @Temporal(TemporalType.TIMESTAMP)
  private OffsetDateTime paidDate;

  @Size(max = 100)
  @NotNull
  @Column(name = "description", nullable = false, length = 100)
  private String description;

  @NotNull
  @Column(name = "invoice_total", nullable = false, precision = 12, scale = 2)
  private BigDecimal invoiceTotal;

  @Size(max = 20)
  @Column(name = "payment_receipt_number", length = 20)
  private String paymentReceiptNumber;

  @Size(max = 20)
  @Column(name = "invoice_number", length = 20)
  private String invoiceNumber;

  @Size(max = 255)
  @Column(name = "payment_receipt_url")
  private String paymentReceiptUrl;

  @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
  private List<InvoiceLineItem> lineItems;

  public void addLineItem(InvoiceLineItem lineItem) {
    if (lineItems == null) {
      lineItems = new ArrayList<>();
    }
    lineItems.add(lineItem);
    if (invoiceTotal == null) {
      setInvoiceTotal(BigDecimal.ZERO);
    }
    setInvoiceTotal(getInvoiceTotal().add(lineItem.getAmount()));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    Invoice invoice = (Invoice) o;

    return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(paymentTransactionId, invoice.paymentTransactionId)
        .append(externalRef, invoice.externalRef)
        .append(invoiceType, invoice.invoiceType)
        .append(paidDate, invoice.paidDate)
        .append(description, invoice.description)
        .append(invoiceTotal, invoice.invoiceTotal)
        .append(paymentReceiptNumber, invoice.paymentReceiptNumber)
        .append(invoiceNumber, invoice.invoiceNumber)
        .append(paymentReceiptUrl, invoice.paymentReceiptUrl)
        .append(lineItems, invoice.lineItems)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .appendSuper(super.hashCode())
        .append(paymentTransactionId)
        .append(externalRef)
        .append(invoiceType)
        .append(paidDate)
        .append(description)
        .append(invoiceTotal)
        .append(paymentReceiptNumber)
        .append(invoiceNumber)
        .append(paymentReceiptUrl)
        .append(lineItems)
        .toHashCode();
  }
}
