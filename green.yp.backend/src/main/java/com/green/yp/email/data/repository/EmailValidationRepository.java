package com.green.yp.email.data.repository;

import com.green.yp.email.data.model.EmailValidation;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmailValidationRepository extends JpaRepository<EmailValidation, UUID> {
  Optional<EmailValidation> findByExternRefAndEmailAddress(String externRef, String emailAddress);

  Optional<EmailValidation> findByEmailAddress(String emailAddress);

  @Modifying
  @Query("""
    DELETE FROM EmailValidation ev WHERE ev.externRef=:externRef 
""")
  void deleteByExternRef(@Param("externRef") String externRef);

  <T> Optional<T> findByEmailAddressAndExternRef(String emailAddress, String externRef);
}
