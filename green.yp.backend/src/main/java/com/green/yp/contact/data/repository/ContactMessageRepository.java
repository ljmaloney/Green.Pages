package com.green.yp.contact.data.repository;

import com.green.yp.api.apitype.contact.ContactMessageResponse;
import com.green.yp.contact.data.model.ContactMessage;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, UUID> {
    @Query("SELECT c FROM ContactMessage c WHERE c.createDate BETWEEN :startDate AND :endDate ORDER BY c.createDate DESC")
    List<ContactMessageResponse> findMessages(@NotNull @Param("startDate") OffsetDateTime startDate,
                                              @NotNull @Param("endDate") OffsetDateTime endDate);
}
