package com.green.yp.api.apitype.classified;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ClassifiedCustomerResponse(UUID customerId,
                                         OffsetDateTime createDate,
                                         OffsetDateTime updateDate,
                                         String firstName,
                                         String lastName,
                                         String address,
                                         String city,
                                         String state,
                                         String postalCode,
                                         String phoneNumber,
                                         String emailAddress) {}
