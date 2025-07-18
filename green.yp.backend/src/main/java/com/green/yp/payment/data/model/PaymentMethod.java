package com.green.yp.payment.data.model;

import com.green.yp.common.data.embedded.Mutable;
import com.green.yp.payment.data.converter.JsonCardConvertor;
import com.green.yp.payment.data.enumeration.PaymentMethodStatusType;
import com.green.yp.payment.data.json.Card;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@Setter
@Entity
@Table(name = "payment_method", schema = "greenyp")
public class PaymentMethod extends Mutable {

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private PaymentMethodStatusType statusType;

  @Size(max = 50)
  @Column(name = "reference_id", nullable = false, length = 50)
  private String referenceId;

  @Size(max = 50)
  @Column(name = "extern_cust_ref", length = 50)
  private String externCustRef;

  @Size(max = 100)
  @Column(name = "card_ref", length = 100)
  private String cardRef;

  @Column(name = "cancel_date")
  @Temporal(TemporalType.TIMESTAMP)
  private OffsetDateTime cancelDate;

  @Size(max = 100)
  @Column(name = "given_name", length = 100)
  private String givenName;

  @Size(max = 100)
  @Column(name = "family_name", length = 100)
  private String familyName;

  @Size(max = 150)
  @Column(name = "company_name", length = 150)
  private String companyName;

  @Size(max = 100)
  @NotNull
  @Column(name = "payor_address_1", nullable = false, length = 100)
  private String payorAddress1;

  @Size(max = 100)
  @Column(name = "payor_address_2", length = 100)
  private String payorAddress2;

  @Size(max = 100)
  @NotNull
  @Column(name = "payor_city", nullable = false, length = 100)
  private String payorCity;

  @Size(max = 100)
  @NotNull
  @Column(name = "payor_state", nullable = false, length = 100)
  private String payorState;

  @Size(max = 16)
  @NotNull
  @Column(name = "payor_postal_code", nullable = false, length = 16)
  private String payorPostalCode;

  @Size(max = 20)
  @Column(name = "phone_number", length = 20)
  private String phoneNumber;

  @Size(max = 150)
  @Column(name = "email_address", length = 150)
  private String emailAddress;

  @Column(name="card_details")
  @Convert(converter = JsonCardConvertor.class)
  private Card cardDetails;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    PaymentMethod that = (PaymentMethod) o;

    return new EqualsBuilder().appendSuper(super.equals(o)).append(statusType, that.statusType).append(referenceId, that.referenceId).append(externCustRef, that.externCustRef).append(cardRef, that.cardRef).append(cancelDate, that.cancelDate).append(givenName, that.givenName).append(familyName, that.familyName).append(companyName, that.companyName).append(payorAddress1, that.payorAddress1).append(payorAddress2, that.payorAddress2).append(payorCity, that.payorCity).append(payorState, that.payorState).append(payorPostalCode, that.payorPostalCode).append(phoneNumber, that.phoneNumber).append(emailAddress, that.emailAddress).append(cardDetails, that.cardDetails).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(statusType).append(referenceId).append(externCustRef).append(cardRef).append(cancelDate).append(givenName).append(familyName).append(companyName).append(payorAddress1).append(payorAddress2).append(payorCity).append(payorState).append(payorPostalCode).append(phoneNumber).append(emailAddress).append(cardDetails).toHashCode();
  }
}
