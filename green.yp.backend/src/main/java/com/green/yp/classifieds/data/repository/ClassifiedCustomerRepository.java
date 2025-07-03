package com.green.yp.classifieds.data.repository;

import com.green.yp.classifieds.data.model.ClassifiedCustomer;
import jakarta.validation.constraints.Email;
import java.util.Optional;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassifiedCustomerRepository extends JpaRepository<ClassifiedCustomer, UUID> {
  Optional<ClassifiedCustomer> findClassifiedCustomerByEmailAddress(@Email String emailAddress);

  Optional<ClassifiedCustomer> findClassifiedCustomerByEmailAddressOrPhoneNumber(@Email String emailAddress,
                                                                                 @NotBlank(message = "Enter your phone number")
                                                                                 @Pattern(regexp = "^(?:\\+1)?\\s?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$")
                                                                                 String phoneNumber);
}
