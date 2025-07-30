package com.green.yp.producer.data.model;

import com.green.yp.api.apitype.producer.enumeration.InvoiceCycleType;
import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "producer_subscription", schema = "greenyp")
public class ProducerSubscription extends Mutable {

  @Column(name = "producer_id", nullable = false, insertable = false, updatable = false)
  private UUID producerId;

  @NotNull
  @NonNull
  @Column(name = "subscription_id", nullable = false, updatable = false)
  private UUID subscriptionId;

  @NotNull
  @NonNull
  @Column(name = "next_invoice_date")
  @Temporal(TemporalType.DATE)
  private LocalDate nextInvoiceDate;

  @NotNull
  @NonNull
  @Column(name = "start_date", nullable = false)
  @Temporal(TemporalType.DATE)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  @Temporal(TemporalType.DATE)
  private LocalDate endDate;

  @ManyToOne
  @JoinColumn(name = "producer_id", nullable = false)
  private Producer producer;

  @NotNull
  @NonNull
  @Enumerated(EnumType.STRING)
  @Column(name = "invoice_cycle_type", nullable = false)
  private InvoiceCycleType invoiceCycleType;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    ProducerSubscription that = (ProducerSubscription) o;

    return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(producerId, that.producerId)
        .append(subscriptionId, that.subscriptionId)
        .append(nextInvoiceDate, that.nextInvoiceDate)
        .append(startDate, that.startDate)
        .append(endDate, that.endDate)
        .append(producer, that.producer)
        .append(invoiceCycleType, that.invoiceCycleType)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .appendSuper(super.hashCode())
        .append(producerId)
        .append(subscriptionId)
        .append(nextInvoiceDate)
        .append(startDate)
        .append(endDate)
        .append(producer)
        .append(invoiceCycleType)
        .toHashCode();
  }
}
