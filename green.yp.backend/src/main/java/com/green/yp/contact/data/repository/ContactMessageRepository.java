package com.green.yp.contact.data.repository;

import com.green.yp.api.apitype.contact.ContactMessageResponse;
import com.green.yp.contact.data.model.ContactMessage;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, UUID> {
    List<ContactMessageResponse> findByCreateDateBetweenSortedByCreatedDateDesc(@NotNull OffsetDateTime startDate,
                                                                                @NotNull OffsetDateTime endDate);
}
