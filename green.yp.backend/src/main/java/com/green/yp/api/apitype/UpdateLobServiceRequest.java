package com.green.yp.api.apitype;

import com.green.yp.api.apitype.enumeration.CreatedByType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Data;
import lombok.NonNull;

@Data
public class UpdateLobServiceRequest {
    @NonNull
    @NotNull
    private UUID lobServiceId;
    @NonNull
    @NotNull
    private String createdByReference;
    @NonNull
    @NotNull
    private CreatedByType createdByType;

    @NonNull
    @NotNull
    @Size(min = 0, max = 50, message = "The service name must be less than 50 characters")
    @Pattern(regexp = "/([A-Za-z\\s\\d\\-])\\w+/gix")
    private String serviceName;

    private String serviceDescription;
}
