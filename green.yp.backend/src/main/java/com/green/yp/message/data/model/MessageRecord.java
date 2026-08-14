package com.green.yp.message.data.model;

import com.green.yp.api.apitype.contact.ContactMessageRequestType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MessageRecord(UUID messageId,
                            OffsetDateTime createDateTime,
                            OffsetDateTime messageSentDate,
                            OffsetDateTime messageReadDate,
                            String sourceIpAddress,
                            ContactMessageRequestType requestType,
                            MessageStatus messageStatus,
                            UUID parentSourceRef,
                            UUID sourceRef,
                            String addresseeName,
                            String destination,
                            String fromEmail,
                            String fromPhone,
                            String title,
                            String message
    ) {}
