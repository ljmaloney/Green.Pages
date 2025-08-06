package com.green.yp.search.controller;

import com.green.yp.api.apitype.PageableResponse;
import com.green.yp.api.apitype.search.SearchResponse;
import com.green.yp.search.service.SearchV2Service;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v2/search")
@Tag(name = "Endpoints supporting searching using various criteria from the public pages")
public class SearchV2Controller {

    private final SearchV2Service searchService;

    public SearchV2Controller(SearchV2Service searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public PageableResponse<SearchResponse> search(
            @RequestParam String zipCode,
            @RequestParam String keywords,
            @RequestParam(required = false) UUID categoryRefId,
            @RequestParam(defaultValue = "25") Integer distance,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "15") Integer limit) {

        Pageable pageable = PageRequest.of(page, limit);
        return searchService.search(zipCode, distance, categoryRefId, keywords, pageable);
    }
}
