package com.green.yp.email.data.repository;

import com.green.yp.api.apitype.contact.ContactMessageRequestType;
import com.green.yp.api.apitype.contact.ContactMessageResponse;
import com.green.yp.email.data.model.ContactMessage;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, UUID> {
  @Query(
      "SELECT c FROM ContactMessage c WHERE c.createDate BETWEEN :startDate AND :endDate ORDER BY c.createDate DESC")
  List<ContactMessageResponse> findMessages(
      @NotNull @Param("startDate") OffsetDateTime startDate,
      @NotNull @Param("endDate") OffsetDateTime endDate);

  @Query("""
        SELECT msg
        FROM ContactMessage msg
        WHERE msg.createDate BETWEEN :startDate AND :endDate
         AND msg.contactRequestType = :requestType
    """)
  List<ContactMessage> findContactMessages(
      @NotNull @NonNull @Param("startDate") OffsetDateTime startDate,
      @NotNull @NonNull @Param("endDate") OffsetDateTime endDate,
      @NotNull @NonNull @Param("requestType") ContactMessageRequestType requestType);
}
