package com.green.yp.classifieds.service;

import com.green.yp.api.apitype.classified.ClassifiedAdCustomerResponse;
import com.green.yp.api.apitype.classified.ClassifiedResponse;
import com.green.yp.classifieds.data.repository.ClassifiedRepository;
import com.green.yp.classifieds.mapper.ClassifiedMapper;
import com.green.yp.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class ClassifiedService {

    private final ClassifiedRepository repository;
    private final ClassifiedMapper mapper;

    public ClassifiedService(ClassifiedRepository repository,
                             ClassifiedMapper mapper){
        this.repository = repository;
        this.mapper = mapper;
    }

    public ClassifiedAdCustomerResponse findClassified(UUID classifiedId) {
        return repository.findClassifiedAndCustomer(classifiedId)
                .map(mapper::fromProjection)
                .orElseThrow( () -> {
                    log.warn("No classified ad found for id {}", classifiedId);
                    return new NotFoundException("Classified", classifiedId);
                });
    }
}
