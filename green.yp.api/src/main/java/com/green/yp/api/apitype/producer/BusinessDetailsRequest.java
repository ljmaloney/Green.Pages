package com.green.yp.api.apitype.producer;

import com.green.yp.api.apitype.producer.enumeration.ProducerSubscriptionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.NonNull;

public record BusinessDetailsRequest(UUID producerId,
                                     @NotNull
                                     @NonNull
                                     @Size(max = 100, message = "The business name must be less than 100 characters in length")
                                     String businessName,
                                     @NonNull
                                     @NotNull(message = "The line of business must be less than 50 characters in length")
                                     String lineOfBusiness,
                                     UUID lineOfBusinessId,
                                     @NotNull @NonNull UUID subscriptionId,
                                     @NotNull @NonNull ProducerSubscriptionType subscriptionType,
                                     @Size(max = 200, message = "The website URL must be less than 200 characters in length")
                                     String websiteUrl,
                                     @Size(max = 512)
                                     String narrative) {
}
