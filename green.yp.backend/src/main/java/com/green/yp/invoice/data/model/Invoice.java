package com.green.yp.invoice.data.model;

import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "producer_invoice", schema = "greenyp")
public class Invoice extends Mutable {

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
}
