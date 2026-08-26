package com.green.yp.api.message;

import java.util.UUID;

public record MessageMetaResponse(UUID metaId,
                                  String subject,
                                  String messageDescription,
                                  Long unreadMessageCount) {}
