package com.green.yp.invoice.data.model;

import com.green.yp.common.data.embedded.Immutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "invoice_line_item", schema = "greenyp")
public class InvoiceLineItem extends Immutable {

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "invoice_id", nullable = false)
  private Invoice invoice;

  @Size(max = 50)
  @NotNull
  @Column(name = "external_ref_1", nullable = false, length = 50)
  private String externalRef1;

  @Size(max = 50)
  @NotNull
  @Column(name = "external_ref_2", nullable = false, length = 50)
  private String externalRef2;

  @Column(name = "line_item")
  private Integer lineItem;

  @Size(max = 255)
  @NotNull
  @Column(name = "description", nullable = false)
  private String description;

  @NotNull
  @Column(name = "amount", nullable = false, precision = 12, scale = 2)
  private BigDecimal amount;
}
