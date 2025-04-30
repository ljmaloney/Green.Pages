package com.green.yp.reference.dto;

import com.green.yp.reference.data.enumeration.LineOfBusinessCreateType;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LineOfBusinessDto {
    private UUID lineOfBusinessId;

    private String createByReference;

    private OffsetDateTime createDate;

    @Builder.Default
    private LineOfBusinessCreateType createType = LineOfBusinessCreateType.SYSTEM_DEFAULT;

    @NotNull
    private String description;

    @NotNull
    private Boolean enableDistanceRadius;

    private OffsetDateTime lastUpdateDate;

    @NotNull
    @NonNull
    private String lineOfBusiness;
}
