package com.green.yp.invoice.data.model;

import com.green.yp.common.data.embedded.Immutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
  @Column(name = "external_ref_1", nullable = false, length = 50)
  private String externalRef1;

  @Column(name = "external_ref_2", nullable = false, length = 50)
  private String externalRef2;

  @Column(name = "line_item")
  private Integer lineNumber;

  @Column(name = "quantity")
  private Integer quantity;

  @Size(max = 255)
  @NotNull
  @Column(name = "description", nullable = false)
  private String description;

  @NotNull
  @Column(name = "amount", nullable = false, precision = 12, scale = 2)
  private BigDecimal amount;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    InvoiceLineItem that = (InvoiceLineItem) o;

    return new EqualsBuilder().appendSuper(super.equals(o)).append(invoice, that.invoice).append(externalRef1, that.externalRef1).append(externalRef2, that.externalRef2).append(lineNumber, that.lineNumber).append(quantity, that.quantity).append(description, that.description).append(amount, that.amount).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(invoice).append(externalRef1).append(externalRef2).append(lineNumber).append(quantity).append(description).append(amount).toHashCode();
  }
}
