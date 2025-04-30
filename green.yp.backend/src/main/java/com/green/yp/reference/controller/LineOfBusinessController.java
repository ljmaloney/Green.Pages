package com.green.yp.reference.controller;

import com.green.yp.api.apitype.CreateLobServiceRequest;
import com.green.yp.api.apitype.UpdateLobServiceRequest;
import com.green.yp.common.controller.BaseRestController;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.reference.dto.LOBServiceDto;
import com.green.yp.reference.dto.LineOfBusinessDto;
import com.green.yp.reference.mapper.LineOfBusinessMapper;
import com.green.yp.reference.service.LineOfBusinessService;
import com.green.yp.util.RequestUtil;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Validated
@RequestMapping("reference")
public class LineOfBusinessController extends BaseRestController {

  private final LineOfBusinessService lobService;

  private final LineOfBusinessMapper lobMapper;

  public LineOfBusinessController(
      LineOfBusinessService lobService, LineOfBusinessMapper lobMapper) {
    this.lobService = lobService;
    this.lobMapper = lobMapper;
  }

  @GetMapping(value = "/lob", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<List<LineOfBusinessDto>> getLineOfBusiness() {
    return new ResponseApi<>(lobMapper.toApi(lobService.getAllLineOfBusiness()), null);
  }

  @GetMapping(value = "/lob/{lineOfBusiness}/service", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<List<LOBServiceDto>> findService(
      @PathVariable("lineOfBusiness") UUID lineOfBusinessId) {
    return new ResponseApi<>(lobService.findServices(lineOfBusinessId), null);
  }

  @PostMapping(
      path = "/lob",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<LineOfBusinessDto> createLineOfBusiness(
      @RequestBody LineOfBusinessDto lineOfBusiness) {
    return new ResponseApi<>(
        lobService.createLineOfBusiness(lineOfBusiness, null, RequestUtil.getRequestIP()), null);
  }

  @PostMapping(
      value = "/lob/service",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<LOBServiceDto> createService(
      @RequestBody CreateLobServiceRequest serviceRequest) {
    return new ResponseApi<>(
        lobService.createService(serviceRequest, null, RequestUtil.getRequestIP()), null);
  }

  @PutMapping(
      path = "/lob/description",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<LineOfBusinessDto> updateDescription(LineOfBusinessDto lobDto) {
    return new ResponseApi<>(
        lobService.updateLineOfBusiness(lobDto, null, RequestUtil.getRequestIP()), null);
  }

  @PutMapping(
      value = "/lob/service",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<LOBServiceDto> updateService(
      @RequestBody UpdateLobServiceRequest serviceRequest) {
    return new ResponseApi<>(
        lobService.updateService(serviceRequest, null, RequestUtil.getRequestIP()), null);
  }
}
