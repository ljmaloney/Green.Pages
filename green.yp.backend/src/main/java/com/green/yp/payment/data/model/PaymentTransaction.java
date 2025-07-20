package com.green.yp.payment.data.model;

import com.green.yp.common.data.embedded.Mutable;
import com.green.yp.payment.data.converter.JsonCardDetailsConvertor;
import com.green.yp.payment.data.json.CardDetails;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "payment_transaction", schema = "greenyp")
public class PaymentTransaction extends Mutable {

  @Size(max = 255)
  @Column(name = "payment_ref", nullable = false)
  private String paymentRef;

  @Size(max = 50)
  @Column(name = "location_ref", length = 50)
  private String locationRef;

  @Size(max = 50)
  @Column(name = "order_ref", length = 50)
  private String orderRef;

  @Size(max = 50)
  @Column(name = "customer_ref", length = 50)
  private String customerRef;

  @Size(max = 10)
  @Column(name = "receipt_number", length = 10)
  private String receiptNumber;

  @Column(name = "ip_address")
  private String ipAddress;

  @Size(max = 50)
  @Column(name = "status", length = 50)
  private String status;

  @Column(name = "error_message")
  private String errorMessage;

  @Column(name = "error_code")
  private Integer errorCode;

  @Size(max = 50)
  @Column(name = "source_type", length = 50)
  private String sourceType;

  @Column(name = "amount", precision = 12, scale = 2)
  private BigDecimal amount;

  @Column(name = "app_fee_amount", precision = 12, scale = 2)
  private BigDecimal appFeeAmount;

  @Column(name = "approved_amount", precision = 12, scale = 2)
  private BigDecimal approvedAmount;

  @Column(name = "processing_fee", precision = 12, scale = 2)
  private BigDecimal processingFee;

  @Column(name = "refund_amount", precision = 12, scale = 2)
  private BigDecimal refundAmount;

  @Column(name = "total_amount", precision = 12, scale = 2)
  private BigDecimal totalAmount;

  @Size(max = 5)
  @ColumnDefault("'USD'")
  @Column(name = "currency_code", length = 5)
  private String currencyCode;

  @Size(max = 100)
  @NotNull
  @Column(name = "first_name", nullable = false, length = 100)
  private String firstName;

  @Size(max = 100)
  @NotNull
  @Column(name = "last_name", nullable = false, length = 100)
  private String lastName;

  @Size(max = 100)
  @NotNull
  @Column(name = "address", nullable = false, length = 100)
  private String address;

  @Size(max = 100)
  @NotNull
  @Column(name = "city", nullable = false, length = 100)
  private String city;

  @Size(max = 2)
  @NotNull
  @Column(name = "state", nullable = false, length = 2)
  private String state;

  @Size(max = 10)
  @NotNull
  @Column(name = "postal_code", nullable = false, length = 10)
  private String postalCode;

  @Size(max = 20)
  @NotNull
  @Column(name = "phone_number", nullable = false, length = 15)
  private String phoneNumber;

  @Size(max = 100)
  @NotNull
  @Column(name = "email_address", nullable = false, length = 100)
  private String emailAddress;

  @Lob
  @Column(name = "statement_description_identifier")
  private String statementDescriptionIdentifier;

  @Size(max = 255)
  @Column(name = "receipt_url")
  private String receiptUrl;

  @Size(max = 255)
  @Column(name = "version_token")
  private String versionToken;

  @Column(name = "error_body")
  private String errorBody;

  @Column(name = "payment_details")
  @Convert(converter = JsonCardDetailsConvertor.class)
  private CardDetails paymentDetails;

  @NotNull
  @Lob
  @Column(name = "note", nullable = false)
  private String note;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    PaymentTransaction that = (PaymentTransaction) o;

    return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(paymentRef, that.paymentRef)
        .append(locationRef, that.locationRef)
        .append(orderRef, that.orderRef)
        .append(customerRef, that.customerRef)
        .append(receiptNumber, that.receiptNumber)
        .append(status, that.status)
        .append(sourceType, that.sourceType)
        .append(amount, that.amount)
        .append(appFeeAmount, that.appFeeAmount)
        .append(approvedAmount, that.approvedAmount)
        .append(processingFee, that.processingFee)
        .append(refundAmount, that.refundAmount)
        .append(totalAmount, that.totalAmount)
        .append(currencyCode, that.currencyCode)
        .append(firstName, that.firstName)
        .append(lastName, that.lastName)
        .append(address, that.address)
        .append(city, that.city)
        .append(state, that.state)
        .append(postalCode, that.postalCode)
        .append(phoneNumber, that.phoneNumber)
        .append(emailAddress, that.emailAddress)
        .append(statementDescriptionIdentifier, that.statementDescriptionIdentifier)
        .append(receiptUrl, that.receiptUrl)
        .append(versionToken, that.versionToken)
        .append(paymentDetails, that.paymentDetails)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .appendSuper(super.hashCode())
        .append(paymentRef)
        .append(locationRef)
        .append(orderRef)
        .append(customerRef)
        .append(receiptNumber)
        .append(status)
        .append(sourceType)
        .append(amount)
        .append(appFeeAmount)
        .append(approvedAmount)
        .append(processingFee)
        .append(refundAmount)
        .append(totalAmount)
        .append(currencyCode)
        .append(firstName)
        .append(lastName)
        .append(address)
        .append(city)
        .append(state)
        .append(postalCode)
        .append(phoneNumber)
        .append(emailAddress)
        .append(statementDescriptionIdentifier)
        .append(receiptUrl)
        .append(versionToken)
        .append(paymentDetails)
        .toHashCode();
  }
}
