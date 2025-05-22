package com.green.yp.reference.dto;

import com.green.yp.reference.data.enumeration.LineOfBusinessCreateType;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;

@Builder
public record LineOfBusinessDto (UUID lineOfBusinessId,
                                 OffsetDateTime createDate,
                                 OffsetDateTime lastUpdateDate,
                                 @NotNull
                                 String lineOfBusinessName, LineOfBusinessCreateType createType,
                                 String createByReference,
                                 String description,
                                 Boolean enableDistanceRadius,
                                 String iconName,
                                 String iconFileName){
    public LineOfBusinessDto {
       if ( createType == null )
           createType = LineOfBusinessCreateType.SYSTEM_DEFAULT;
    }
}
