package com.green.yp.email.data.model;

import com.green.yp.api.apitype.email.EmailValidationStatusType;
import com.green.yp.common.data.embedded.Mutable;
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
@Table(name = "email_validation", schema = "greenyp")
public class EmailValidation extends Mutable {

    @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "email_validation_date")
  private OffsetDateTime emailValidationDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "validation_status", length = 50)
  private EmailValidationStatusType validationStatus;

  @Size(max = 50)
  @Column(name = "ip_address", length = 50)
  private String ipAddress;

  @Size(max = 50)
  @Column(name = "extern_ref", length = 50)
  private String externRef;

  @Size(max = 50)
  @Column(name = "email_token", length = 50)
  private String emailToken;

  @Size(max = 150)
  @NotNull
  @Column(name = "email_address", nullable = false, length = 150)
  private String emailAddress;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    EmailValidation that = (EmailValidation) o;

    return new EqualsBuilder().appendSuper(super.equals(o)).append(emailValidationDate, that.emailValidationDate).append(validationStatus, that.validationStatus).append(ipAddress, that.ipAddress).append(externRef, that.externRef).append(emailToken, that.emailToken).append(emailAddress, that.emailAddress).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(emailValidationDate).append(validationStatus).append(ipAddress).append(externRef).append(emailToken).append(emailAddress).toHashCode();
  }
}
