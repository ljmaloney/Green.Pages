package com.green.yp.classifieds.service;

import com.green.yp.api.apitype.classified.ClassifiedAdCustomerResponse;
import com.green.yp.api.apitype.classified.ClassifiedRequest;
import com.green.yp.api.apitype.classified.ClassifiedResponse;
import com.green.yp.classifieds.data.repository.ClassifiedCustomerRepository;
import com.green.yp.classifieds.data.repository.ClassifiedRepository;
import com.green.yp.classifieds.mapper.ClassifiedMapper;
import com.green.yp.exception.NotFoundException;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClassifiedService {

  private final ClassifiedRepository repository;
  private final ClassifiedCustomerRepository customerRepository;
  private final ClassifiedAdTypeService adTypeService;
  private final ClassifiedCategoryService categoryService;
  private final ClassifiedGeocodeService geocodeService;
  private final ClassifiedMapper mapper;

  public ClassifiedService(
      ClassifiedRepository repository,
      ClassifiedCustomerRepository customerRepository,
      ClassifiedAdTypeService adTypeService,
      ClassifiedCategoryService categoryService,
      ClassifiedGeocodeService geocodeService,
      ClassifiedMapper mapper) {
    this.repository = repository;
    this.customerRepository = customerRepository;
    this.adTypeService = adTypeService;
    this.categoryService = categoryService;
    this.geocodeService = geocodeService;
    this.mapper = mapper;
  }

  public ClassifiedAdCustomerResponse findClassifiedAndCustomer(UUID classifiedId) {
    return repository
        .findClassifiedAndCustomer(classifiedId)
        .map(mapper::fromProjection)
        .orElseThrow(
            () -> {
              log.warn("No classified ad and customer found for id {}", classifiedId);
              return new NotFoundException("Classified", classifiedId);
            });
  }

  public ClassifiedResponse findClassified(UUID classifiedId, String requestIP) {
    return repository
        .findById(classifiedId)
        .map(mapper::fromEntity)
        .orElseThrow(
            () -> {
              log.warn("No classified found for id {}", classifiedId);
              return new NotFoundException("Classified", classifiedId);
            });
  }

  public ClassifiedResponse createClassified(@Valid ClassifiedRequest request) {
    // upsert customer record if not already found
    var customer =
        customerRepository
            .findClassifiedCustomerByEmailAddressOrPhoneNumber(request.emailAddress(), request.phoneNumber())
            .orElseGet(
                () -> {
                  return customerRepository.saveAndFlush(mapper.customterFromClassified(request));
                });

    adTypeService.findAdType(request.adType());
    categoryService.findCategory(request.categoryId());

    var classified = mapper.toEntity(request);

    var geoLocation = geocodeService.geocodeLocation(classified, request.address());

    classified.setClassifiedCustomerId(customer.getId());
    classified.setActiveDate(null);
    classified.setLastActiveDate(null);
    classified.setRenewalCount(0);
    classified.setLatitude(geoLocation.latitude());
    classified.setLongitude(geoLocation.longitude());

    return mapper.fromEntity(repository.saveAndFlush(classified));
  }
}
