package com.green.yp.api.apitype.email;

import java.util.UUID;

public record EmailValidationResponse(UUID validationId,
                                      String emailAddress,
                                      EmailValidationStatusType validationStatus,
                                      String token) {}
