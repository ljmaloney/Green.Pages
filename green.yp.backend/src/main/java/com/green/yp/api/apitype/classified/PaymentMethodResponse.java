package com.green.yp.api.apitype.classified;

import java.lang.foreign.ValueLayout;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PaymentMethodResponse(UUID paymentMethodId,
                                    OffsetDateTime createDate,
                                    OffsetDateTime lastUpdateDate,
                                    OffsetDateTime cancelDate,
                                    Boolean active,
                                    String referenceId,
                                    String externCustRef,
                                    String cardRef,
                                    String givenName,
                                    String familyName,
                                    String companyName,
                                    String payorAddress1,
                                    String payorAddress2,
                                    String payorCity,
                                    String payorState,
                                    String payorPostalCode,
                                    String phoneNumber,
                                    String emailAddress) {}
