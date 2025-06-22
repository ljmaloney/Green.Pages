package com.green.yp.producer.controller;

import com.green.yp.api.apitype.common.ResponseApi;
import com.green.yp.producer.service.ProducerCsvImportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("producer")
@Tag(name = "Producer Import Controller")
@Slf4j
@RequiredArgsConstructor
public class ProducerImportController {
  private final ProducerCsvImportService importService;

  @PostMapping(
      path = "/uploadCSV",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<List<UUID>> uploadCsv(
      @RequestParam("file") MultipartFile file,
      @RequestParam("lineOfBusinessId") UUID lineOfBusinessId) {
    log.info("Processing CSV upload for lineOfBusiness: {}", lineOfBusinessId);
    List<UUID> createdProducerIds = importService.importProducersFromCsv(file, lineOfBusinessId);
    return new ResponseApi<>(createdProducerIds, null);
  }
}
