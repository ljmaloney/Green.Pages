package com.green.yp.message.data.model;

import com.green.yp.api.apitype.contact.ContactMessageRequestType;
import com.green.yp.common.data.embedded.Mutable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Table(name="message_meta", schema = "greenyp")
public class MessageMeta extends Mutable {

    @Column(name = "source_ip_address")
    private String sourceIpAddress;

    @Size(max = 50)
    @Column(name = "sms_email_type", nullable = false, length = 50)
    private String smsEmailType;

    @Enumerated(EnumType.STRING)
    @Column(name="message_status", nullable = false, length = 50)
    private MessageStatus messageStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "contact_request_type", nullable = false, length = 50)
    private ContactMessageRequestType contactRequestType;

    @Column(name = "source_ref")
    private UUID sourceRef;

    @Column(name="parent_source_ref")
    private UUID parentSourceRef;

    @Column(name = "requestor_ref")
    private UUID requestor_ref;

    @Column(name = "requestor_name", length = 150)
    private String requestorName;

    @Size(max = 150)
    @NotNull
    @Column(name = "requestor_email", nullable = false, length = 150)
    private String fromEmail;

    @Size(max = 15)
    @Column(name = "requestor_phone", length = 15)
    private String fromPhone;

    @Size(max = 255)
    @NotNull
    @Column(name = "subject", nullable = false)
    private String subject;
}
