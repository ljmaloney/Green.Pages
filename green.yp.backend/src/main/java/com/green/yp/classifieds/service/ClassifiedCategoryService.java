package com.green.yp.classifieds.service;

import com.green.yp.api.apitype.classified.ClassifiedCategoryResponse;
import com.green.yp.classifieds.data.repository.ClassifiedCategoryRepository;
import com.green.yp.classifieds.mapper.ClassifiedCategtoryMapper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClassifiedCategoryService {

  private final ClassifiedCategoryRepository repository;
  private final ClassifiedCategtoryMapper mapper;

  public ClassifiedCategoryService(
      ClassifiedCategoryRepository repository, ClassifiedCategtoryMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Cacheable("classifiedCategory")
  public List<ClassifiedCategoryResponse> getCategoryList() {
    return repository.findAll().stream().map(mapper::fromEntity).toList();
  }
}
