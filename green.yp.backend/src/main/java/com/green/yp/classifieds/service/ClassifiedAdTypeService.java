package com.green.yp.classifieds.service;

import com.green.yp.api.apitype.classified.ClassifiedAdTypeResponse;
import com.green.yp.classifieds.data.repository.ClassifiedAdTypeRepository;
import com.green.yp.classifieds.mapper.ClassifiedAdTypeMapper;
import com.green.yp.exception.NotFoundException;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClassifiedAdTypeService {
  private final ClassifiedAdTypeRepository repository;
  private final ClassifiedAdTypeMapper mapper;

  public ClassifiedAdTypeService(
      ClassifiedAdTypeRepository repository, ClassifiedAdTypeMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Cacheable("classifiedAdTypes")
  public List<ClassifiedAdTypeResponse> getActiveAdTypes() {
    return repository.findClassifiedAdTypeByActiveOrderBySortOrderAsc(Boolean.TRUE).stream()
        .map(mapper::fromEntity)
        .toList();
  }

  @Cacheable("classifiedAdTypes")
  public ClassifiedAdTypeResponse findAdType(@NotNull @NonNull UUID adTypeId) {
    return repository
        .findById(adTypeId)
        .map(mapper::fromEntity)
        .orElseThrow(
            () -> {
              log.warn("no classified ad type found for {}", adTypeId);
              return new NotFoundException("ClassifiedAdType", adTypeId);
            });
  }
}
