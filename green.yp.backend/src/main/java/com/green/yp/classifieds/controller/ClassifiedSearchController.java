package com.green.yp.classifieds.controller;

import com.green.yp.api.apitype.classified.ClassifiedSearchResponse;
import com.green.yp.classifieds.service.ClassifiedSearchService;
import com.green.yp.common.dto.ResponseApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequestMapping("classified")
@Validated
@Tag(name = "Endpoints for managing images uploaded for a classified ad")
@RestController
public class ClassifiedSearchController {

    private ClassifiedSearchService searchService;

    public ClassifiedSearchController(ClassifiedSearchService searchService){
        this.searchService = searchService;
    }

    @GetMapping(path="search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<List<ClassifiedSearchResponse>> search(@RequestParam(value = "postalCode", required = true) String postalCode,
                                                              @RequestParam("distance") Integer distanceMiles,
                                                              @RequestParam(value ="category", required = false) UUID categoryId,
                                                              @RequestParam(value="keywords", required = false) String keywords) {
        return null;
    }

    @GetMapping(path="mostRecent", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<List<ClassifiedSearchResponse>> mostRecent(@RequestParam(value = "number", defaultValue = "9") Integer maxCount,
                                                                  @RequestParam(value = "categoryId", required = false) UUID categoryId){
    return new ResponseApi<>(searchService.mostRecent(maxCount, categoryId), null);
    }
}
