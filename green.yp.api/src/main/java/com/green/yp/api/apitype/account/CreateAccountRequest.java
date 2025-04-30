package com.green.yp.api.apitype.account;

import com.green.yp.api.apitype.producer.enumeration.ProducerSubscriptionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {
    @NotNull
    @NonNull
    @Size(max = 100, message = "The business name must be less than 100 characters in length")
    private String businessName;

    @NonNull
    @NotNull(message = "The line of business must be less than 50 characters in length")
    private UUID lineOfBusinessId;

    @NotNull
    @NonNull
    private UUID subscriptionId;

    @NotNull
    @NonNull
    private ProducerSubscriptionType subscriptionType;

    @Size(max = 200, message = "The website URL must be less than 200 characters in length")
    private String websiteUrl;

    @Size(max = 512, message = "The business narrative must be less than 512 characters in length")
    private String narrative;
}
