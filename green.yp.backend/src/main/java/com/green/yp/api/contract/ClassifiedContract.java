package com.green.yp.api.contract;

import com.green.yp.api.apitype.classified.ClassifiedAdCustomerResponse;
import com.green.yp.classifieds.service.ClassifiedService;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ClassifiedContract {
    private final ClassifiedService classifiedService;
    public ClassifiedContract(ClassifiedService service){
        this.classifiedService = service;
    }

    public ClassifiedAdCustomerResponse findClassifiedAd(UUID classifiedId) {
        return classifiedService.findClassifiedAndCustomer(classifiedId);
    }
}
