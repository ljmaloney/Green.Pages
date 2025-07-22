package com.green.yp.classifieds.data.repository;

import com.green.yp.classifieds.data.model.ClassifiedCustomer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClassifiedCustomerRepository extends JpaRepository<ClassifiedCustomer, UUID> {
  Optional<ClassifiedCustomer> findClassifiedCustomerByEmailAddress(@Email String emailAddress);

  Optional<ClassifiedCustomer> findClassifiedCustomerByEmailAddressOrPhoneNumber(@Email String emailAddress,
                                                                                 @NotBlank(message = "Enter your phone number")
                                                                                 @Pattern(regexp = "^(?:\\+1)?\\s?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$")
                                                                                 String phoneNumber);

    @NotNull Optional<ClassifiedCustomer> findClassifiedCustomerByPhoneNumber(@NotBlank(message = "Enter your phone number")
                                                                              @Pattern(regexp = "^(?:\\+1)?\\s?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$")
                                                                              String phoneNumber);
}
