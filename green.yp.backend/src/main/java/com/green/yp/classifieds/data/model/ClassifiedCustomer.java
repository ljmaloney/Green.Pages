package com.green.yp.classifieds.data.model;

import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
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
}
