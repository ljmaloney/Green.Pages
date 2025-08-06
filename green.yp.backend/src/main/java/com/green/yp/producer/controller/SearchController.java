package com.green.yp.producer.controller;

import com.green.yp.api.apitype.PageableResponse;
import com.green.yp.api.apitype.search.ProducerSearchResponse;
import com.green.yp.producer.service.ProducerSearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
@Tag(name = "Endpoints supporting searching using various criteria from the public pages")
public class SearchController {
  private final ProducerSearchService searchService;

  public SearchController(ProducerSearchService searchService) {
    this.searchService = searchService;
  }

  @GetMapping
  public PageableResponse<ProducerSearchResponse> search(
      @RequestParam String zipCode,
      @RequestParam(defaultValue = "25") Integer distance,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "15") Integer limit,
      @RequestParam(required = false) UUID categoryId,
      @RequestParam(required = false) UUID serviceId) {

    Pageable pageable = PageRequest.of(page, limit);
    return searchService.search(zipCode, distance, pageable, categoryId, serviceId);
  }
}
