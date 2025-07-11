package com.green.yp.classifieds.data.model;

import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Getter
@Setter
@Table(name = "classified_customer")
public class ClassifiedCustomer extends Mutable {
  @NotNull
  @NonNull
  @Column(name = "first_name", nullable = false, length = 100)
  private String firstName;

  @NotNull
  @NonNull
  @Column(name = "last_name", nullable = false, length = 100)
  private String lastName;

  @NotNull
  @NonNull
  @Column(name = "address", nullable = false, length = 100)
  private String address;

  @NotNull
  @NonNull
  @Column(name = "city", nullable = false, length = 100)
  private String city;

  @NotNull
  @NonNull
  @Column(name = "state", nullable = false, length = 2)
  private String state;

  @NotNull
  @NonNull
  @Size(max = 10)
  @Column(name = "postal_code", nullable = false, length = 10)
  private String postalCode;

  @NotNull
  @NonNull
  @Size(max = 15)
  @Column(name = "phone_number", nullable = false, length = 15)
  private String phoneNumber;

  @NotNull
  @NonNull
  @Size(max = 100)
  @Column(name = "email_address", nullable = false, length = 100)
  private String emailAddress;

  @Size(max = 25)
  @Column(name="email_validation_token", nullable = false, length = 25)
  private String emailAddressValidationToken;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="email_validation_date")
  private OffsetDateTime emailValidationDate;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    ClassifiedCustomer that = (ClassifiedCustomer) o;

    return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(firstName, that.firstName)
        .append(lastName, that.lastName)
        .append(address, that.address)
        .append(city, that.city)
        .append(state, that.state)
        .append(postalCode, that.postalCode)
        .append(phoneNumber, that.phoneNumber)
        .append(emailAddress, that.emailAddress)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .appendSuper(super.hashCode())
        .append(firstName)
        .append(lastName)
        .append(address)
        .append(city)
        .append(state)
        .append(postalCode)
        .append(phoneNumber)
        .append(emailAddress)
        .toHashCode();
  }
}
