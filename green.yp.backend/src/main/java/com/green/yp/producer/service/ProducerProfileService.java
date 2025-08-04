package com.green.yp.producer.service;

import com.green.yp.api.apitype.producer.ProducerProfileResponse;
import com.green.yp.api.apitype.search.TruncatedProducerResponse;
import com.green.yp.api.contract.LineOfBusinessContract;
import com.green.yp.exception.NotFoundException;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.producer.data.repository.ProducerSearchRepository;
import com.green.yp.producer.mapper.ProducerSearchMapper;
import java.util.List;
import java.util.UUID;

import com.green.yp.reference.dto.LineOfBusinessDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProducerProfileService {

  private final ProducerSearchRepository searchRepository;
  private final ProducerSearchMapper producerSearchMapper;
  private final LineOfBusinessContract lineOfBusinessContract;

  public ProducerProfileService(
      ProducerSearchRepository searchRepository,
      ProducerSearchMapper producerSearchMapper,
      LineOfBusinessContract lineOfBusinessContract) {
    this.searchRepository = searchRepository;
    this.producerSearchMapper = producerSearchMapper;
    this.lineOfBusinessContract = lineOfBusinessContract;
  }

  public ProducerProfileResponse getProducerProfile(UUID producerLocationId) {
    log.info("Getting producer profile for locationId: {}", producerLocationId);
    return searchRepository
        .findProducerProfile(producerLocationId)
        .map(producerSearchMapper::toProfileResponse)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "Producer profile not found for locationId: " + producerLocationId));
  }

  public List<TruncatedProducerResponse> getProfiles(
          UUID lineOfBusinessId, String lobUrl, Boolean mostRecent, Integer maxLimit) {
    log.info(
        "Searching for producers with lineOfBusinessId: {}, limit: {}",
            StringUtils.isBlank(lobUrl) ? lineOfBusinessId : lobUrl, maxLimit);

    if (StringUtils.isBlank(lobUrl) && lineOfBusinessId == null ) {
      log.warn("Missing required parameter, either lobUrl or lineOfBusinessId is required");
      throw new PreconditionFailedException("Missing required parameter, either lobUrl or lineOfBusinessId is required");
    }

    LineOfBusinessDto lobDto = null;
    if ( StringUtils.isNotBlank(lobUrl) ) {
      lobDto = lineOfBusinessContract.findLineOfBusiness(lobUrl);
    } else {
      lobDto = lineOfBusinessContract.findLineOfBusiness(lineOfBusinessId);
    }

    var results = searchRepository.findMostRecentProfiles(lobDto.lineOfBusinessId(), Limit.of(maxLimit));

    log.info("Found {} producers for lineOfBusinessId: {}", results.size(), lobDto.lineOfBusinessId());

    return producerSearchMapper.limitedOutputResponse(results);
  }
}
