package com.green.yp.producer.invoice.data.model;

import com.green.yp.common.data.embedded.Immutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.stereotype.Service;

@Entity
@Getter
@Service
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "producer_invoice_lineitem", schema = "greenyp")
public class InvoiceLineItem extends Immutable {

  @NotNull
  @NonNull
  @Column(name = "line_item", updatable = false, nullable = false)
  private Integer lineItem;

  @NotNull
  @NonNull
  @Column(name = "producer_id", updatable = false, nullable = false)
  private UUID producerId;

  @NotNull
  @NonNull
  @Column(name = "subscription_id", updatable = false, nullable = false)
  private UUID subscriptionId;

  @NotNull
  @NonNull
  @Column(name = "producer_invoice_id", insertable = false, updatable = false, nullable = false)
  private UUID producerInvoiceId;

  @NotNull
  @NonNull
  @Column(name = "description", updatable = false, nullable = false)
  private String description;

  @NotNull
  @NonNull
  @Column(name = "amount", updatable = false, nullable = false, columnDefinition = "DECIMAL(10,2)")
  private BigDecimal amount;

  @ManyToOne
  @JoinColumn(name = "producer_invoice_id")
  private Invoice invoice;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    InvoiceLineItem that = (InvoiceLineItem) o;

    return new EqualsBuilder().appendSuper(super.equals(o)).append(lineItem, that.lineItem).append(producerId, that.producerId).append(subscriptionId, that.subscriptionId).append(producerInvoiceId, that.producerInvoiceId).append(description, that.description).append(amount, that.amount).append(invoice, that.invoice).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(lineItem).append(producerId).append(subscriptionId).append(producerInvoiceId).append(description).append(amount).append(invoice).toHashCode();
  }
}
