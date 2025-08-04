package com.green.yp.reference.service;

import com.green.yp.api.AuditRequest;
import com.green.yp.api.apitype.CreateLobServiceRequest;
import com.green.yp.api.apitype.UpdateLobServiceRequest;
import com.green.yp.api.apitype.enumeration.AuditActionType;
import com.green.yp.api.apitype.enumeration.AuditObjectType;
import com.green.yp.exception.ErrorCodeType;
import com.green.yp.exception.NotFoundException;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.reference.data.model.LOBService;
import com.green.yp.reference.data.model.LineOfBusiness;
import com.green.yp.reference.data.repository.LOBServiceRepository;
import com.green.yp.reference.data.repository.LineOfBusinessRepository;
import com.green.yp.reference.dto.LOBServiceDto;
import com.green.yp.reference.dto.LineOfBusinessDto;
import com.green.yp.reference.mapper.LineOfBusinessMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LineOfBusinessService {

  private final LineOfBusinessRepository repository;
  private final LOBServiceRepository lobServiceRepository;
  private final LineOfBusinessMapper lineOfBusinessMapper;

  private static final String LINE_OF_BUSINESS = "lineOfBusiness";

  @Autowired
  public LineOfBusinessService(
      LineOfBusinessRepository repository,
      LOBServiceRepository lobServiceRepository,
      LineOfBusinessMapper mapper) {
    this.repository = repository;
    this.lobServiceRepository = lobServiceRepository;
    this.lineOfBusinessMapper = mapper;
  }

  @Cacheable("lineOfBusiness")
  public LineOfBusinessDto getLineOfBusiness(UUID lineOfBusiness) {
    Optional<LineOfBusiness> lineOfBusinessOptional = repository.findById(lineOfBusiness);

    LineOfBusiness lob =
        lineOfBusinessOptional.orElseThrow(
            () -> new NotFoundException(LINE_OF_BUSINESS, lineOfBusiness));

    return lineOfBusinessMapper.toDto(lob);
  }

  @Cacheable("lineOfBusiness")
  public LineOfBusinessDto getLineOfBusiness(String urlString){
    return repository.findLineOfBusinessByUrlLob(urlString)
            .map(lineOfBusinessMapper::toDto)
            .orElseThrow(() -> new NotFoundException(LINE_OF_BUSINESS, urlString));
  }

  @Cacheable("allLineOfBusiness")
  public List<LineOfBusinessDto> getAllLineOfBusiness() {

    List<LineOfBusiness> lobList = repository.findAllOrderByLineOfBusinessAsc();

    if (CollectionUtils.isEmpty(lobList)) {
      throw new NotFoundException("No lines of business found");
    }
    return lineOfBusinessMapper.toDto(lobList);
  }

  @AuditRequest(
      requestParameter = "lobDto",
      actionType = AuditActionType.CREATE_LINE_OF_BUSINESS,
      objectType = AuditObjectType.LINE_OF_BUSINESS)
  @CacheEvict(value = "allLineOfBusiness", allEntries = true)
  public LineOfBusinessDto createLineOfBusiness(
      @NotNull LineOfBusinessDto lobDto, String userId, @NotNull String ipAddress) {

    if (lobDto.lineOfBusinessId() != null) {
      throw new PreconditionFailedException(
          "Attempting to create new line of business and lineOfBusinessId is not null");
    }

    Optional<LineOfBusiness> lineOfBusinessOptional =
        repository.findLineOfBusinessByLineOfBusinessName(lobDto.lineOfBusinessName());
    if (lineOfBusinessOptional.isPresent()) {
      throw new PreconditionFailedException(
          ErrorCodeType.LINE_OF_BUSINESS_EXISTS.getMessageFormat(), lobDto.lineOfBusinessName());
    }

    LineOfBusiness lob = lineOfBusinessMapper.fromDto(lobDto);

    lob = repository.saveAndFlush(lob);

    return lineOfBusinessMapper.toDto(lob);
  }

  @AuditRequest(
      requestParameter = "lobDto",
      objectType = AuditObjectType.LINE_OF_BUSINESS,
      actionType = AuditActionType.UPDATE)
  @CacheEvict(
      value = {"allLineOfBusiness", LINE_OF_BUSINESS},
      allEntries = true)
  public LineOfBusinessDto updateLineOfBusinessDescription(
      @NotNull LineOfBusinessDto lobDto, String userId, @NotNull String ipAddress) {
    Optional<LineOfBusiness> lineOfBusinessOptional =
        repository.findById(lobDto.lineOfBusinessId());

    LineOfBusiness lineOfBusiness =
        lineOfBusinessOptional.orElseThrow(
            () -> new NotFoundException("LineOfBusiness", lobDto.lineOfBusinessName()));

    lineOfBusiness.setDescription(lobDto.description());

    lineOfBusiness = repository.saveAndFlush(lineOfBusiness);

    return lineOfBusinessMapper.toDto(lineOfBusiness);
  }

  @AuditRequest(
          requestParameter = "lobDto",
          objectType = AuditObjectType.LINE_OF_BUSINESS,
          actionType = AuditActionType.UPDATE)
  @CacheEvict(
          value = {"allLineOfBusiness", LINE_OF_BUSINESS},
          allEntries = true)
  public LineOfBusinessDto updateLineOfBusiness(@Valid @NotNull LineOfBusinessDto lineOfBusinessDto, String userId, @NotNull String ipAddress) {
    var lineOfBusiness = repository.findById(lineOfBusinessDto.lineOfBusinessId()).orElseThrow(
            () -> new NotFoundException("LineOfBusiness", lineOfBusinessDto.lineOfBusinessId()));

    return lineOfBusinessMapper.toDto(
            repository.save(lineOfBusinessMapper
                    .fromDtoToEntity(lineOfBusinessDto, lineOfBusiness)));
  }

  public List<LOBServiceDto> findServices(UUID lineOfBusinessId) {
    Optional<LineOfBusiness> lineOfBusinessOptional = repository.findById(lineOfBusinessId);

    LineOfBusiness lob =
        lineOfBusinessOptional.orElseThrow(
            () -> new NotFoundException(LINE_OF_BUSINESS, lineOfBusinessId));

    List<LOBService> services = lobServiceRepository.findLOBServicesByLineOfBusinessId(lob.getId());

    if (CollectionUtils.isEmpty(services)) {
      throw new NotFoundException("LineOfBusinessService", lineOfBusinessId);
    }
    return lineOfBusinessMapper.fromEntity(services);
  }

  @AuditRequest(
      requestParameter = "serviceRequest",
      objectType = AuditObjectType.CREATE_LOB_SERVICE,
      actionType = AuditActionType.CREATE)
  public LOBServiceDto createService(
      CreateLobServiceRequest serviceRequest, String userId, String ipAddress) {
    getLineOfBusiness(serviceRequest.getLineOfBusinessId());

    LOBService lobService =
        lobServiceRepository.saveAndFlush(lineOfBusinessMapper.toEntity(serviceRequest));

    return lineOfBusinessMapper.fromEntity(lobService);
  }

  @AuditRequest(
      requestParameter = "serviceRequest",
      objectType = AuditObjectType.UPDATE_LOB_SERVICE,
      actionType = AuditActionType.CREATE)
  public LOBServiceDto updateService(
      UpdateLobServiceRequest serviceRequest, String userId, String ipAddress) {
    LOBService lobService =
        lobServiceRepository
            .findById(serviceRequest.getLobServiceId())
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "LineOfBusiness_ServiceId", serviceRequest.getLobServiceId()));

    lobService.setServiceName(serviceRequest.getServiceName());
    lobService.setServiceDescription(serviceRequest.getServiceDescription());
    lobService.setCreatedByReference(serviceRequest.getCreatedByReference());
    lobService.setCreatedByType(serviceRequest.getCreatedByType());

    lobService = lobServiceRepository.saveAndFlush(lobService);

    return lineOfBusinessMapper.fromEntity(lobService);
  }

  public LineOfBusinessDto findLineOfBusiness(UUID lineOfBusinessId) {
    return getLineOfBusiness(lineOfBusinessId);
  }
}
