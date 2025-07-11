package com.green.yp.producer.invoice.data.model;

import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "producer_invoice", schema = "greenyp")
public class ProducerInvoice extends Mutable {

  @Column(name = "producer_id", updatable = false, nullable = false)
  private UUID producerId;

  @Column(name = "subscription_id", updatable = false, nullable = false)
  private UUID subscriptionId;

  @Column(name = "producer_subscription_id", updatable = false, nullable = false)
  private UUID producerSubscriptionId;

  @Column(name = "paid_date")
  @Temporal(TemporalType.TIMESTAMP)
  private OffsetDateTime paidDate;

  @Column(name = "invoice_number", length = 20, nullable = false, updatable = false)
  private String printedInvoiceId;

  @Column(name = "invoice_total")
  private BigDecimal invoiceTotal;

  @OneToMany(mappedBy = "producerInvoice", cascade = CascadeType.ALL)
  private List<ProducerInvoiceLineItem> lineItems;

  public void addLineItem(ProducerInvoiceLineItem lineItem) {
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

    ProducerInvoice producerInvoice = (ProducerInvoice) o;

    return new EqualsBuilder().appendSuper(super.equals(o)).append(producerId, producerInvoice.producerId).append(subscriptionId, producerInvoice.subscriptionId).append(producerSubscriptionId, producerInvoice.producerSubscriptionId).append(paidDate, producerInvoice.paidDate).append(printedInvoiceId, producerInvoice.printedInvoiceId).append(invoiceTotal, producerInvoice.invoiceTotal).append(lineItems, producerInvoice.lineItems).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(producerId).append(subscriptionId).append(producerSubscriptionId).append(paidDate).append(printedInvoiceId).append(invoiceTotal).append(lineItems).toHashCode();
  }
}
