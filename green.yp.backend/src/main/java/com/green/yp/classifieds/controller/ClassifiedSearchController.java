package com.green.yp.classifieds.controller;

import com.green.yp.api.apitype.classified.ClassifiedSearchResponse;
import com.green.yp.classifieds.service.ClassifiedSearchService;
import com.green.yp.common.dto.GenericPageableResponse;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.util.RequestUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("classified")
@Validated
@Tag(name = "Endpoints for managing images uploaded for a classified ad")
@RestController
public class ClassifiedSearchController {

    private final ClassifiedSearchService searchService;

    public ClassifiedSearchController(ClassifiedSearchService searchService){
        this.searchService = searchService;
    }

    @GetMapping(path="search", produces = MediaType.APPLICATION_JSON_VALUE)
    public GenericPageableResponse<ClassifiedSearchResponse> search(@RequestParam(value = "postalCode") String postalCode,
                                                                    @RequestParam(value = "distance", defaultValue = "50") Integer distanceMiles,
                                                                    @RequestParam(value ="category", required = false) UUID categoryId,
                                                                    @RequestParam(value="keywords", required = false) String keywords,
                                                                    @RequestParam(defaultValue = "0") Integer page,
                                                                    @RequestParam(defaultValue = "15") Integer limit,
                                                                    HttpServletRequest httpRequest ) {
        Pageable pageable = PageRequest.of(page, limit);
        return searchService.searchClassifieds(pageable,
                postalCode,
                distanceMiles,
                categoryId,
                keywords,
                RequestUtil.getRequestIP(httpRequest));
    }

    @GetMapping(path="mostRecent", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<List<ClassifiedSearchResponse>> mostRecent(@RequestParam(value = "number", defaultValue = "9") Integer maxCount,
                                                                  @RequestParam(value = "categoryId", required = false) UUID categoryId,
                                                                  @RequestParam(value = "categoryName", required = false) String categoryName,
                                                                  HttpServletRequest httpRequest){
    return new ResponseApi<>(searchService.mostRecent(maxCount, categoryId, categoryName, RequestUtil.getRequestIP(httpRequest)), null);
    }
}
