package com.green.yp.payment.data.model;

import com.green.yp.common.data.converter.BooleanConverter;
import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "payment_method", schema = "greenyp")
public class PaymentMethod extends Mutable {

  @NotNull
  @Convert(converter = BooleanConverter.class)
  @Column(name = "active", nullable = false)
  private Boolean active;

  @Size(max = 50)
  @NotNull
  @Column(name = "reference_id", nullable = false, length = 50)
  private String referenceId;

  @Size(max = 50)
  @Column(name = "extern_cust_ref", length = 50)
  private String externCustRef;

  @Size(max = 100)
  @Column(name = "card_ref", length = 100)
  private String cardRef;

  @Column(name = "cancel_date")
  private Instant cancelDate;

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
}
