package com.green.yp.classifieds.data.repository;

import com.green.yp.classifieds.data.model.ClassifiedCustomer;
import jakarta.validation.constraints.Email;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassifiedCustomerRepository extends JpaRepository<ClassifiedCustomer, UUID> {
  Optional<ClassifiedCustomer> findClassifiedCustomerByEmailAddress(@Email String emailAddress);
}
