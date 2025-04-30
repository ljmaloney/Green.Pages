package com.green.yp.api.apitype;

import com.green.yp.api.apitype.enumeration.CreatedByType;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;
import lombok.NonNull;

@Data
public class CreateLobServiceRequest {
    @NotNull
    @NonNull
    private UUID lineOfBusinessId;

    @NotNull
    @NonNull
    private String createdByReference;

    @NotNull
    @NonNull
    private CreatedByType createdByType;

    @NotNull
    @NonNull
    private String serviceName;

    private String serviceDescription;

}
