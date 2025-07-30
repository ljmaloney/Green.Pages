package com.green.yp.producer.data.model;

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
@Table(name = "invalid_credentials_counter", schema = "greenyp")
public class InvalidCredentialsCounter {
  @NonNull
  @NotNull
  @Id
  @Column(name = "id", updatable = false)
  private UUID id;

  @NotNull
  @NonNull
  @Column(name = "created_date", updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private OffsetDateTime createdDate;

  @NotNull
  @NonNull
  @Column(name = "user_id", updatable = false)
  private String userId;

  @NonNull
  @NotNull
  @Column(name = "bad_creds", updatable = false)
  private String badCreds;

  @NonNull
  @NotNull
  @Column(name = "ip_address", updatable = false)
  private String ipAddress;

  @NonNull
  @NotNull
  @Column(name = "user_credentials_id", updatable = false)
  private UUID userCredentialsId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    InvalidCredentialsCounter that = (InvalidCredentialsCounter) o;

    return new EqualsBuilder()
        .append(id, that.id)
        .append(createdDate, that.createdDate)
        .append(userId, that.userId)
        .append(badCreds, that.badCreds)
        .append(ipAddress, that.ipAddress)
        .append(userCredentialsId, that.userCredentialsId)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(id)
        .append(createdDate)
        .append(userId)
        .append(badCreds)
        .append(ipAddress)
        .append(userCredentialsId)
        .toHashCode();
  }
}
