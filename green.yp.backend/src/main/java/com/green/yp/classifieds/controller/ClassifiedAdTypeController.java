package com.green.yp.classifieds.controller;

import com.green.yp.api.apitype.classified.ClassifiedAdTypeResponse;
import com.green.yp.classifieds.service.ClassifiedAdTypeService;
import com.green.yp.common.dto.ResponseApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Validated
@RequestMapping("reference/classified")
@Tag(name = "REST endpoint types of adds for classifieds")
public class ClassifiedAdTypeController {

  private final ClassifiedAdTypeService service;

  public ClassifiedAdTypeController(ClassifiedAdTypeService service) {
    this.service = service;
  }

  @Operation(summary = "Returns the list of available ad types")
  @GetMapping(value = "/ad/types", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<List<ClassifiedAdTypeResponse>> getClassifiedAdType() {
    return new ResponseApi(service.getActiveAdTypes(), null);
  }
}
