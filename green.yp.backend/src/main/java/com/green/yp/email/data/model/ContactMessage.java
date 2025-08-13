package com.green.yp.email.data.model;

import com.green.yp.common.data.embedded.Mutable;
import com.green.yp.api.apitype.contact.ProducerContactRequestType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "email_contact_message", schema = "greenyp")
public class ContactMessage extends Mutable {

  @Column(name = "message_sent_date")
  private OffsetDateTime messageSentDate;

  @Column(name="source_ip_address")
  private String sourceIpAddress;

  @Size(max = 50)
  @NotNull
  @Column(name = "sms_email_type", nullable = false, length = 50)
  private String smsEmailType;

  @Size(max = 50)
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "contact_request_type", nullable = false, length = 50)
  private ProducerContactRequestType contactRequestType;

  @Column (name = "producer_id")
  private UUID producerId;

  @Column(name = "location_id")
  private UUID locationId;

  @Column(name = "classified_id")
  private UUID classifiedId;

  @Size(max = 16)
  @Column(name = "product_service_ref", length = 16)
  private UUID productServiceRef;

  @Column(name="addressee_name")
  private String addresseeName;

  @Size(max = 150)
  @NotNull
  @Column(name = "destination", nullable = false, length = 150)
  private String destination;

  @Size(max = 150)
  @NotNull
  @Column(name = "from_email", nullable = false, length = 150)
  private String fromEmail;

  @Size(max = 15)
  @Column(name = "from_phone", length = 15)
  private String fromPhone;

  @Size(max = 255)
  @NotNull
  @Column(name = "title", nullable = false)
  private String title;

  @Lob
  @Column(name = "message")
  private String message;
}
