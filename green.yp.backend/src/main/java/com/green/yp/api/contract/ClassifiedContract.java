package com.green.yp.api.contract;

import com.green.yp.api.apitype.classified.ClassifiedResponse;
import com.green.yp.classifieds.service.ClassifiedService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ClassifiedContract {
    private final ClassifiedService classifiedService;
    public ClassifiedContract(ClassifiedService service){
        this.classifiedService = service;
    }

    public ClassifiedResponse findClassified(UUID classifiedId) {
        return classifiedService.findClassified(classifiedId);
    }
}
