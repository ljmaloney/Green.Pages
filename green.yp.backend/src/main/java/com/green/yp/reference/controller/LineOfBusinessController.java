package com.green.yp.reference.controller;

import com.green.yp.api.apitype.CreateLobServiceRequest;
import com.green.yp.api.apitype.UpdateLobServiceRequest;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.reference.dto.LOBServiceDto;
import com.green.yp.reference.dto.LineOfBusinessDto;
import com.green.yp.reference.mapper.LineOfBusinessMapper;
import com.green.yp.reference.service.LineOfBusinessService;
import com.green.yp.util.RequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "REST endpoint to manage supported lines of business")
public class LineOfBusinessController {

  private final LineOfBusinessService lobService;

  private final LineOfBusinessMapper lobMapper;

  public LineOfBusinessController(
      LineOfBusinessService lobService, LineOfBusinessMapper lobMapper) {
    this.lobService = lobService;
    this.lobMapper = lobMapper;
  }

  @Operation(summary = "Returns a list of the active lines of business")
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", description = "No lines of business configured / active")
  @GetMapping(value = "/lob", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<List<LineOfBusinessDto>> getLineOfBusiness() {
    return new ResponseApi<>(lobMapper.toApi(lobService.getAllLineOfBusiness()), null);
  }

  @Operation(summary = "Returns a list of the services offered by a line of business")
  @ApiResponse(responseCode = "200")
  @ApiResponse(responseCode = "404", description = "No services found for a line of business")
  @GetMapping(value = "/lob/{lineOfBusiness}/service", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<List<LOBServiceDto>> findService(
      @PathVariable("lineOfBusiness") UUID lineOfBusinessId) {
    return new ResponseApi<>(lobService.findServices(lineOfBusinessId), null);
  }

  @Operation(summary = "Creates a new line of business")
  @ApiResponse(responseCode = "200", description = "Line of business created")
  @ApiResponse(responseCode = "412", description = "Precondition for line of business creation failed")
  @PostMapping(
      path = "/lob",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<LineOfBusinessDto> createLineOfBusiness(
      @RequestBody LineOfBusinessDto lineOfBusiness) {
    return new ResponseApi<>(
        lobService.createLineOfBusiness(lineOfBusiness, null, RequestUtil.getRequestIP()), null);
  }

  @Operation(summary = "Creates a new service of a line of business")
  @ApiResponse(responseCode = "200", description = "New service added to a line of business")
  @ApiResponse(responseCode = "404", description = "Line of business for service not found")
  @PostMapping(
      value = "/lob/service",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<LOBServiceDto> createService(
      @RequestBody CreateLobServiceRequest serviceRequest) {
    return new ResponseApi<>(
        lobService.createService(serviceRequest, null, RequestUtil.getRequestIP()), null);
  }

  @Operation(summary = "Updates a description for a line of business")
  @ApiResponse(responseCode = "200")
  @PutMapping(
      path = "/lob/description",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<LineOfBusinessDto> updateDescription(LineOfBusinessDto lobDto) {
    return new ResponseApi<>(
        lobService.updateLineOfBusiness(lobDto, null, RequestUtil.getRequestIP()), null);
  }

  @Operation(summary = "Updates a line of business")
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
