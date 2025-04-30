package com.green.yp.invoice.data.model;

import com.green.yp.common.data.embedded.Immutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
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
}
