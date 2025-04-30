package com.green.yp.payment.data.model;

import com.green.yp.common.data.embedded.Immutable;
import com.green.yp.payment.data.enumeration.*;
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
@Table(name = "producer_payment_transaction", schema = "greenyp")
public class PaymentTransaction extends Immutable {

    @NotNull
    @NonNull
    @Column(name = "producer_id", nullable = false, updatable = false)
    private UUID producerId;

    @NotNull
    @NonNull
    @Column(name = "payment_method_id", nullable = false, updatable = false)
    private UUID paymentMethodId;

    @NotNull
    @NonNull
    @Column(name = "producer_invoice_id", nullable = false, updatable = false)
    private UUID invoiceId;

    @NotNull
    @NonNull
    @Column(name = "payment_type", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private ProducerPaymentType paymentType;

    @NotNull
    @NonNull
    @Column(name = "amount", nullable = false, updatable = false)
    private BigDecimal amount;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PaymentTransactionStatus status;

    @Column(name = "reference_number", updatable = false)
    private String acquirerReferenceNumber;

    @Convert(converter = AvsErrorResponseCode.AvsErrorResponseCodeConverter.class)
    @Column(name = "avs_error_code", length = 1, updatable = false)
    private AvsErrorResponseCode avsErrorResponseCode;

    @Convert(converter = AvsResponseCode.AvsResponseCodeConverter.class)
    @Column(name = "avs_postal_response_code", length = 1, updatable = false)
    private AvsResponseCode avsPostalCodeResponseCode;

    @Convert(converter = AvsResponseCode.AvsResponseCodeConverter.class)
    @Column(name = "avs_street_addr_response_code", length = 1, updatable = false)
    private AvsResponseCode avsStreetAddressResponseCode;

    @Convert(converter = CvvResponseCode.CvvResponseCodeConverter.class)
    @Column(name = "cvv_response_code", length = 1, updatable = false)
    private CvvResponseCode cvvResponseCode;

    @Column
    private String responseCode;

    @Column
    private String responseText;
}
