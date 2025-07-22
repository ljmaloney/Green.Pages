package com.green.yp.api.apitype.payment;

import com.green.yp.payment.data.enumeration.PaymentMethodStatusType;
import com.green.yp.payment.data.json.Card;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PaymentMethodResponse(UUID paymentMethodId,
                                    OffsetDateTime createDate,
                                    OffsetDateTime lastUpdateDate,
                                    OffsetDateTime cancelDate,
                                    String referenceId,
                                    String externCustRef,
                                    PaymentMethodStatusType statusType,
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
                                    String emailAddress,
                                    Card cardDetails) {}
