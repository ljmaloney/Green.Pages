package com.green.yp.api.apitype.reference;

import com.green.yp.api.enumeration.CreatedByType;
import lombok.Data;

import java.util.UUID;

@Data
public class LOBServiceApi {
  private UUID lobServiceId;
  private String lineOfBusiness;
  private String createdByReference;
  private CreatedByType createdByType;
  private String serviceName;
  private String serviceDescription;
}
