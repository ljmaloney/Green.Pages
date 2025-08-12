package com.green.yp.producer.service;

import com.green.yp.api.AuditRequest;
import com.green.yp.api.apitype.PatchRequest;
import com.green.yp.api.apitype.ProducerServiceResponse;
import com.green.yp.api.apitype.enumeration.AuditActionType;
import com.green.yp.api.apitype.enumeration.AuditObjectType;
import com.green.yp.api.apitype.enumeration.SearchRecordType;
import com.green.yp.api.apitype.producer.ProducerServiceDeleteRequest;
import com.green.yp.api.apitype.producer.ProducerServiceRequest;
import com.green.yp.api.apitype.producer.ProducerServiceUpdateRequest;
import com.green.yp.common.ServiceUtils;
import com.green.yp.exception.BusinessException;
import com.green.yp.exception.NotFoundException;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.producer.data.model.ProducerService;
import com.green.yp.producer.data.repository.ProducerServiceRepository;
import com.green.yp.producer.mapper.ProducerServiceMapper;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ProducerServicesService {

  private final ProducerServiceMapper serviceMapper;
  private final ProducerServiceRepository serviceRepository;
  private final ProducerSearchService searchService;

  public ProducerServicesService(
      ProducerServiceMapper serviceMapper,
      ProducerServiceRepository serviceRepository,
      ProducerSearchService searchService) {
    this.serviceMapper = serviceMapper;
    this.serviceRepository = serviceRepository;
    this.searchService = searchService;
  }

  public List<ProducerServiceResponse> findServices(
      @NotNull @NonNull UUID producerId,
      @NotNull @NonNull UUID producerLocationId,
      @NotNull @NonNull String requestIP) {
    log.info("Loading services for {} at location {}", producerId, producerLocationId);

    List<ProducerService> services = serviceRepository.findServices(producerId, producerLocationId);

    return serviceMapper.fromEntity(services);
  }

  @AuditRequest(
      requestParameter = "serviceRequest",
      objectType = AuditObjectType.PRODUCER_SERVICE,
      actionType = AuditActionType.CREATE)
  @Transactional
  public ProducerServiceResponse createService(
      @NotNull @NonNull ProducerServiceRequest serviceRequest, String userId, String requestIP) {
    log.info(
        "Creating new service [{}] for producer {}",
        serviceRequest.shortDescription(),
        serviceRequest.producerId());

    serviceRepository
        .findProducerService(
            serviceRequest.producerId(),
            serviceRequest.producerLocationId(),
            serviceRequest.shortDescription())
        .ifPresent(
            svc -> {
              throw new PreconditionFailedException(
                  "Attempting to create duplicate service for %s and location %s",
                  serviceRequest.producerId(), serviceRequest.producerLocationId());
            });

    ProducerService service = serviceMapper.toEntity(serviceRequest);

    ProducerService producerService = serviceRepository.saveAndFlush(service);

    var response = serviceMapper.fromEntity(producerService);
    searchService.upsertProducerService(response);
    return response;
  }

  @AuditRequest(
      requestParameter = "updateRequest",
      objectType = AuditObjectType.PRODUCER_SERVICE,
      actionType = AuditActionType.UPDATE)
  @Transactional
  public ProducerServiceResponse updateService(
      ProducerServiceUpdateRequest updateRequest, String requestIP) {
    log.info("Updating service {}", updateRequest.serviceId());

    var service =
        serviceRepository
            .findById(updateRequest.serviceId())
            .orElseThrow(
                () -> {
                  log.warn("No producer service found for id {}", updateRequest.serviceId());
                  return new NotFoundException("ProducerService", updateRequest.serviceId());
                });

    ServiceUtils.updateFromRecord(service, updateRequest, "serviceId");

    ProducerService producerService = serviceRepository.saveAndFlush(service);
    var response = serviceMapper.fromEntity(producerService);
    searchService.upsertProducerService(response);
    return response;
  }

  @AuditRequest(
      requestParameter = "patchRequest",
      objectType = AuditObjectType.PRODUCER_SERVICE,
      actionType = AuditActionType.UPDATE)
  public ProducerServiceResponse patchService(
      @NotNull @NonNull UUID serviceId,
      @NotNull @NonNull PatchRequest patchRequest,
      String userId,
      String requestIP) {

    ProducerService producerService =
        serviceRepository
            .findById(serviceId)
            .orElseThrow(() -> new NotFoundException("ProducerService", serviceId));

    try {
      ServiceUtils.patchEntity(
          patchRequest,
          producerService,
          (name, value) ->
              switch (name) {
                case "minServicePrice" -> BigDecimal.valueOf((Double) value);
                case "maxServicePrice" -> BigDecimal.valueOf((Double) value);
                default -> value;
              });
      return serviceMapper.fromEntity(serviceRepository.save(producerService));

    } catch (Exception e) {
      log.error("Exception occurred while updating producer service id: {}", serviceId, e);
      throw new BusinessException("Unexpected error patching ProducerService", e);
    }
  }

  public void deleteService(@NotNull @NonNull UUID serviceId, String userId, String requestIP) {
    log.info("Deleting service {} from {}", serviceId, requestIP);
    serviceRepository.deleteById(serviceId);
    searchService.deleteSearch(serviceId, SearchRecordType.GREEN_PRO_SERVICE);
  }

  @Transactional
  public void discontinueService(ProducerServiceDeleteRequest deleteRequest) {
    var service =
        serviceRepository
            .findById(deleteRequest.serviceId())
            .orElseThrow(
                () -> {
                  log.warn("No service found for id {}", deleteRequest.serviceId());
                  throw new NotFoundException("ProducerService", deleteRequest.serviceId());
                });

    service.setDiscontinued(true);
    service.setDiscontinueDate(deleteRequest.discontinueDate());
    serviceRepository.saveAndFlush(service);
    log.info("Discontinued service {} on {}", service.getId(), service.getDiscontinueDate());
  }
}
