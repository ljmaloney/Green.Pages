package com.green.yp.producer.data.model;

import com.green.yp.api.apitype.producer.enumeration.ProducerContactType;
import com.green.yp.api.apitype.producer.enumeration.ProducerDisplayContactType;
import com.green.yp.common.data.converter.BooleanConverter;
import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Size;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// @Builder
@Table(name = "producer_contact", schema = "greenyp")
public class ProducerContact extends Mutable {
  @NotNull
  @Column(name = "producer_id")
  private UUID producerId;

  @Column(name = "producer_location_id")
  private UUID producerLocationId;

  @NotNull
  @Column(name = "contact_type")
  @Enumerated(EnumType.STRING)
  private ProducerContactType producerContactType;

  @NotNull
  @Column(name = "display_type")
  @Enumerated(EnumType.STRING)
  private ProducerDisplayContactType displayContactType;

  @Column(name = "generic_contact_name", length = 50)
  private String genericContactName;

  @Column(name = "first_name", length = 50)
  private String firstName;

  @Column(name = "last_name", length = 50)
  private String lastName;

  @Column(name = "title", length = 50)
  private String title;

  @Column(name = "phone_number", length = 12)
  private String phoneNumber;

  @Column(name = "cell_phone_number", length = 12)
  private String cellPhoneNumber;

  @Convert(converter = BooleanConverter.class)
  @Column(name = "email_confirmed")
  private Boolean emailConfirmed;

  @Column(name = "auth_cancel_date")
  @Temporal(TemporalType.TIMESTAMP)
  private OffsetDateTime authenticationCancelDate;

  @Column(name = "email_confirmed_date")
  @Temporal(TemporalType.TIMESTAMP)
  private OffsetDateTime emailConfirmedDate;

  @Column(name = "email_confirmation_token")
  private String emailConfirmationToken;

  @Column(name = "email_address", length = 150)
  private String emailAddress;

    public boolean isValidEmailToken(@NotNull @NonNull String validationToken) {
      return validationToken.equals(emailConfirmationToken);
    }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    ProducerContact that = (ProducerContact) o;

    return new EqualsBuilder().appendSuper(super.equals(o)).append(producerId, that.producerId).append(producerLocationId, that.producerLocationId).append(producerContactType, that.producerContactType).append(displayContactType, that.displayContactType).append(genericContactName, that.genericContactName).append(firstName, that.firstName).append(lastName, that.lastName).append(title, that.title).append(phoneNumber, that.phoneNumber).append(cellPhoneNumber, that.cellPhoneNumber).append(emailConfirmed, that.emailConfirmed).append(authenticationCancelDate, that.authenticationCancelDate).append(emailConfirmedDate, that.emailConfirmedDate).append(emailConfirmationToken, that.emailConfirmationToken).append(emailAddress, that.emailAddress).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(producerId).append(producerLocationId).append(producerContactType).append(displayContactType).append(genericContactName).append(firstName).append(lastName).append(title).append(phoneNumber).append(cellPhoneNumber).append(emailConfirmed).append(authenticationCancelDate).append(emailConfirmedDate).append(emailConfirmationToken).append(emailAddress).toHashCode();
  }
}
