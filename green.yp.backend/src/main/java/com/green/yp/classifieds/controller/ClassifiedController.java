package com.green.yp.classifieds.controller;

import com.green.yp.api.apitype.classified.ClassifiedRequest;
import com.green.yp.api.apitype.classified.ClassifiedResponse;
import com.green.yp.api.apitype.classified.ClassifiedUpdateRequest;
import com.green.yp.api.apitype.enumeration.ClassifiedTokenType;
import com.green.yp.classifieds.service.ClassifiedService;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.util.RequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Validated
@RequestMapping("classified")
@Tag(name = "REST endpoints to create / delete / update / view classified ad")
public class ClassifiedController {
  private final ClassifiedService service;

  public ClassifiedController(ClassifiedService service) {
    this.service = service;
  }

  @GetMapping(path = "{classifiedId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<ClassifiedResponse> findClassified(
      @PathVariable("classifiedId") UUID classifiedId) {
    return new ResponseApi<>(
        service.findClassified(classifiedId, RequestUtil.getRequestIP()), null);
  }

  @Operation(summary = "Requests sending authenticate messsage so classified customer can edit the ad")
  @GetMapping(path="{classifiedId}/requestAuthCode")
  public void requestAuthCode(@PathVariable("classifiedId") UUID classifiedId,
                              @RequestParam(name="tokenDestination")  String tokenDestination,
                              @RequestParam(name="tokenType") ClassifiedTokenType tokenType) throws NoSuchAlgorithmException {
    service.requestAuthCode(classifiedId, tokenDestination, tokenType, RequestUtil.getRequestIP());
  }

  @Operation(summary = "Creates the initial classified ad record")
  @PostMapping( path="create-ad",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<ClassifiedResponse> createClassified(
      @RequestBody @Valid ClassifiedRequest classifiedRequest) {
    return new ResponseApi<>(service.createClassified(classifiedRequest), null);
  }

  @Operation(summary = "Updates a classified ad")
  @PutMapping( consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<ClassifiedResponse> updateClassified(@RequestBody @Valid ClassifiedUpdateRequest classifiedRequest) {
    return new ResponseApi<>(service.updateClassified(classifiedRequest, RequestUtil.getRequestIP()), null);
  }
}
