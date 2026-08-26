package com.green.yp.message.data.repository;

import com.green.yp.api.apitype.contact.ContactMessageRequestType;
import com.green.yp.message.data.model.AppMessageRecord;
import com.green.yp.message.data.model.Message;
import com.green.yp.message.data.model.MessageRecord;
import com.green.yp.message.data.model.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    List<MessageRecord> findContactMessages(@Param("startDateTime") OffsetDateTime startDateTime,
                                            @Param("endDateTime") OffsetDateTime endDateTime,
                                            @Param("requestType") ContactMessageRequestType requestType);

    @Query("""
        SELECT DISTINCT new com.green.yp.message.data.model.AppMessageRecord(message.meta, null,
               (SELECT COUNT(*) from Message msg
                WHERE msg.messageStatus != MessageStatus.ARCHIVED
                    AND ((:emailAddress IS NOT NULL AND msg.fromEmail = :emailAddress OR msg.destination = :emailAddress)
                    OR (:messageRef IS NOT NULL AND msg.fromRef = :messageRef OR msg.addresseeRef = :messageRef)) ))
        FROM MessageMeta meta INNER JOIN Message message
        WHERE
          message.messageStatus != com.green.yp.message.data.model.MessageStatus.ARCHIVED
          AND ((:emailAddress IS NOT NULL AND message.fromEmail = :emailAddress OR message.destination = :emailAddress)
               OR (:messageRef IS NOT NULL AND message.fromRef = :messageRef OR message.addresseeRef = :messageRef))
       """)
    List<AppMessageRecord> findMessages(@Param("emailAddress") String emailAddress,
                                        @Param("messageRef") UUID messageRef);

    @Query("""
        SELECT message
        FROM Message message
        WHERE
          message.messageStatus != com.green.yp.message.data.model.MessageStatus.ARCHIVED
                 AND message.meta.id = :metaId
          AND ((:emailAddress IS NOT NULL AND (message.fromEmail = :emailAddress OR message.destination = :emailAddress))
               OR (:messageRef IS NOT NULL AND (message.fromRef = :messageRef OR message.addresseeRef = :messageRef)))
       """)
    List<Message> findMessages(@Param("metaId") UUID metaId,
                               @Param("emailAddress") String emailAddress,
                               @Param("messageRef") UUID messageRef);

  @Modifying
  @Query(""" 
    UPDATE Message message
    SET message.messageStatus=:messageStatus
    WHERE
        message.id=:messageId
        AND ((:emailAddress IS NOT NULL AND message.destination = :emailAddress)
               OR (:messageRef IS NOT NULL AND message.addresseeRef = :messageRef))
    """)
  int updateStatus(@Param("messageId") UUID messageId,
                   @Param("emailAddress") String emailAddress,
                   @Param("messageRef") UUID messageRef,
                   @Param("messageStatus") MessageStatus status);

    @Modifying
    @Query(""" 
    UPDATE Message message
    SET message.messageStatus=:messageStatus
    WHERE
        message.id=:messageId
        AND ((:emailAddress IS NOT NULL AND (message.fromEmail = :emailAddress OR message.destination = :emailAddress))
               OR (:messageRef IS NOT NULL AND (message.fromRef = :messageRef OR message.addresseeRef = :messageRef)))
    """)
    int archiveMessage(@Param("messageId") UUID messageId,
                     @Param("emailAddress") String emailAddress,
                     @Param("messageRef") UUID messageRef,
                     @Param("messageStatus") MessageStatus status);
}
