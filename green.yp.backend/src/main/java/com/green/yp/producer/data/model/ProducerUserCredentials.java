package com.green.yp.producer.data.model;

import com.green.yp.common.data.converter.BooleanConverter;
import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "producer_user_credentials", schema = "greenyp")
public class ProducerUserCredentials extends Mutable {
  @NotNull
  @NonNull
  @Column(name = "user_id", updatable = false, length = 150)
  private String userId;

  @Column(name = "email_address", updatable = false, length = 150)
  private String emailAddress;

  @Column(name = "producer_id", updatable = false)
  private UUID producerId;

  @Column(name = "producer_contact_id", updatable = false)
  private UUID producerContactId;

  @Column(name = "extern_auth_service_ref", updatable = false, nullable = false, length = 25)
  private String externalAuthorizationServiceRef;

  @Column(name = "registration_ref", updatable = false, nullable = false, length = 25)
  private String registrationRef;

  @NotNull
  @Column(name = "first_name", nullable = false, length = 50)
  private String firstName;

  @NotNull
  @Column(name = "last_name", nullable = false, length = 50)
  private String lastName;

  @Column(name = "password")
  private String password;

  @Column(name = "enabled", length = 1)
  @Convert(converter = BooleanConverter.class)
  private Boolean enabled;

  @Column(name = "last_change_date")
  @Temporal(TemporalType.TIMESTAMP)
  private OffsetDateTime lastChangeDate;

  @Column(name = "admin_user")
  @Convert(converter = BooleanConverter.class)
  private Boolean adminUser;

  @Column(name = "reset_token")
  private UUID resetToken;

  @Column(name = "reset_token_timeout")
  @Temporal(TemporalType.TIMESTAMP)
  private OffsetDateTime resetTokenTimeout;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    ProducerUserCredentials that = (ProducerUserCredentials) o;

    return new EqualsBuilder()
        .appendSuper(super.equals(o))
        .append(userId, that.userId)
        .append(emailAddress, that.emailAddress)
        .append(producerId, that.producerId)
        .append(producerContactId, that.producerContactId)
        .append(externalAuthorizationServiceRef, that.externalAuthorizationServiceRef)
        .append(registrationRef, that.registrationRef)
        .append(firstName, that.firstName)
        .append(lastName, that.lastName)
        .append(password, that.password)
        .append(enabled, that.enabled)
        .append(lastChangeDate, that.lastChangeDate)
        .append(adminUser, that.adminUser)
        .append(resetToken, that.resetToken)
        .append(resetTokenTimeout, that.resetTokenTimeout)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .appendSuper(super.hashCode())
        .append(userId)
        .append(emailAddress)
        .append(producerId)
        .append(producerContactId)
        .append(externalAuthorizationServiceRef)
        .append(registrationRef)
        .append(firstName)
        .append(lastName)
        .append(password)
        .append(enabled)
        .append(lastChangeDate)
        .append(adminUser)
        .append(resetToken)
        .append(resetTokenTimeout)
        .toHashCode();
  }
}
