package com.green.yp.api.contract;

import com.green.yp.reference.dto.LineOfBusinessDto;
import com.green.yp.reference.service.LineOfBusinessService;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class LineOfBusinessContract {
  private final LineOfBusinessService lineOfBusinessService;

  public LineOfBusinessContract(LineOfBusinessService lineOfBusinessService) {
    this.lineOfBusinessService = lineOfBusinessService;
  }

  public LineOfBusinessDto findLineOfBusiness(UUID lineOfBusinessId) {
    return lineOfBusinessService.getLineOfBusiness(lineOfBusinessId);
  }

  public LineOfBusinessDto findLineOfBusiness(String lobUrl) {
    return lineOfBusinessService.getLineOfBusiness(lobUrl);
  }
}
