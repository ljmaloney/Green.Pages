package com.green.yp.email.data.repository;

import com.green.yp.email.data.model.EmailValidation;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailValidationRepository extends JpaRepository<EmailValidation, UUID> {
  Optional<EmailValidation> findByExternRefAndEmailAddress(String externRef, String emailAddress);

  Optional<EmailValidation> findByEmailAddress(String emailAddress);
}
