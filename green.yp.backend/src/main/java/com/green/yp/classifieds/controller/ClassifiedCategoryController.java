package com.green.yp.classifieds.controller;

import com.green.yp.classifieds.apitype.ClassifiedCategoryResponse;
import com.green.yp.classifieds.data.model.ClassifiedCategory;
import com.green.yp.classifieds.service.ClassifiedCategoryService;
import com.green.yp.common.dto.ResponseApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("reference/classified")
@Tag(name = "REST endpoint types of adds for classifieds")
public class ClassifiedCategoryController {

    private final ClassifiedCategoryService service;

    public ClassifiedCategoryController(ClassifiedCategoryService service){
        this.service = service;
    }

    @Operation(summary = "Returns the list of available ad types")
    @GetMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<List<ClassifiedCategoryResponse>> getClassifiedCategories(){
        return new ResponseApi<List<ClassifiedCategoryResponse>>(service.getCategoryList(), null);
    }

}
