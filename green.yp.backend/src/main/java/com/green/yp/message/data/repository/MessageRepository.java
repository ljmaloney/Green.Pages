package com.green.yp.message.data.repository;

import com.green.yp.api.apitype.contact.ContactMessageRequestType;
import com.green.yp.message.data.model.Message;
import com.green.yp.message.data.model.MessageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("""
        SELECT new com.green.yp.message.data.model.MessageRecord(
                m.id,m.createDate,m.messageSentDate, m.readDate,
                m.sourceIpAddress,m.meta.contactRequestType,
                m.messageStatus,m.meta.parentSourceRef,m.meta.sourceRef,
                m.addresseeName,m.destination,
                m.fromEmail,m.fromPhone,
                m.meta.subject,
                m.message) 
        FROM Message m 
        WHERE 
          m.createDate BETWEEN :startDateTime AND :endDateTime 
          AND m.meta.contactRequestType = :requestType
                  """)
    List<MessageRecord> findContactMessages(OffsetDateTime startDateTime,
                                            OffsetDateTime endDateTime,
                                            ContactMessageRequestType requestType);
}
