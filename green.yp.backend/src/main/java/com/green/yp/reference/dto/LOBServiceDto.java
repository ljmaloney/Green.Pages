package com.green.yp.reference.dto;

import com.green.yp.api.apitype.enumeration.CreatedByType;
import java.util.UUID;
import lombok.Data;

@Data
public class LOBServiceDto {
    private UUID lobServiceId;
    private UUID lineOfBusinessId;
    private String createdByReference;
    private CreatedByType createdByType;
    private String serviceName;
    private String serviceDescription;
}
