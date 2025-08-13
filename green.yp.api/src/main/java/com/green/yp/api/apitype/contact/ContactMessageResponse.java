package com.green.yp.api.apitype.contact;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ContactMessageResponse(UUID emailMessageId, OffsetDateTime createDate,
                                     OffsetDateTime messageSendDate,
                                     String sourceIpAddress,
                                     ContactMessageRequestType requestType,
                                     UUID producerId,
                                     UUID locationId,
                                     UUID classifiedId,
                                     String productServiceRef,
                                     String addresseeName,
                                     String destination,
                                     String fromEmail,
                                     String fromPhone,
                                     String title,
                                     String message) {}
