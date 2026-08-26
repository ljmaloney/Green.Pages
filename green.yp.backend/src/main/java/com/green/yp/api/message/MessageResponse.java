package com.green.yp.api.message;

import com.green.yp.message.data.model.MessageStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MessageResponse(OffsetDateTime messageSentDate,
                              OffsetDateTime readDate,
                              UUID messageMetaId,
                              String sourceIpAddress,
                              MessageStatus messageStatus,
                              String smsEmailType,
                              String addresseeName,
                              String destination,
                              UUID addresseeRef,
                              UUID fromRef,
                              String fromEmail,
                              String fromPhone,
                              String message) {}
