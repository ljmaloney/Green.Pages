package com.green.yp.invoice.data.model;

import com.green.yp.api.apitype.invoice.InvoiceType;
import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "invoice", schema = "greenyp")
public class Invoice extends Mutable {

    @Column(name="payment_transaction_id")
    private UUID paymentTransactionId;

    @Size(max = 50)
    @NotNull
    @Column(name = "external_ref", nullable = false, length = 50)
    private String externalRef;

    @Size(max = 50)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_type", nullable = false, length = 50)
    private InvoiceType invoiceType;

    @Column(name = "paid_date")
    @Temporal(TemporalType.DATE)
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

}