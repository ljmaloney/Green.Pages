package com.green.yp.message.data.model;

import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="message", schema = "greenyp")
public class Message extends Mutable {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="message_sent_date")
    OffsetDateTime messageSentDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="read_date")
    private OffsetDateTime readDate;

    @Column(name = "message_meta_id", nullable = false, insertable = false, updatable = false)
    private UUID messageMetaId;

    @Column(name="source_ip_address")
    private String sourceIpAddress;

    @Enumerated(EnumType.STRING)
    @Column(name="message_status", nullable = false, length = 50)
    private MessageStatus messageStatus;

    @Column(name="sms_email_type")
    private String smsEmailType;

    @Column(name="addressee_name")
    private String addresseeName;

    @Column(name="destination")
    private String destination;

    @Column(name="addresseeRef")
    private UUID addresseeRef;

    @Column(name="addressee_email", length = 150)
    private String addresseeEmail;

    @Column(name="from_ref")
    private UUID fromRef;

    @Column(name="from_email")
    private String fromEmail;

    @Column(name="from_phone")
    private String fromPhone;

    @ManyToOne
    @JoinColumn(name = "message_meta_id", nullable = false)
    private MessageMeta meta;

    @Lob
    @Column(name = "message")
    private String message;

}
