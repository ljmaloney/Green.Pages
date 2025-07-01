package com.green.yp.classifieds.service;

import com.green.yp.classifieds.apitype.ClassifiedAdTypeResponse;
import com.green.yp.classifieds.data.repository.ClassifiedAdTypeRepository;
import com.green.yp.classifieds.mapper.ClassifiedAdTypeMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassifiedAdService {
  private final ClassifiedAdTypeRepository repository;
  private final ClassifiedAdTypeMapper mapper;

  public ClassifiedAdService(ClassifiedAdTypeRepository repository, ClassifiedAdTypeMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  public List<ClassifiedAdTypeResponse> getActiveAdTypes() {
    return repository.findClassifiedAdTypeByActiveOrderBySortOrderAsc(Boolean.TRUE)
            .stream()
            .map(mapper::fromEntity)
            .toList();
  }
}
