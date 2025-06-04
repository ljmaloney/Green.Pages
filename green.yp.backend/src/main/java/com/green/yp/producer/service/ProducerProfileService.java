package com.green.yp.producer.service;

import com.green.yp.api.apitype.producer.ProducerProfileResponse;
import com.green.yp.api.apitype.search.ProducerSearchResponse;
import com.green.yp.api.apitype.search.TruncatedProducerResponse;
import com.green.yp.api.contract.LineOfBusinessContract;
import com.green.yp.exception.NotFoundException;
import com.green.yp.producer.data.repository.ProducerSearchRepository;
import com.green.yp.producer.mapper.ProducerSearchMapper;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
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
    return searchRepository.findProducerProfile(producerLocationId)
        .map(producerSearchMapper::toProfileResponse)
        .orElseThrow(() -> new NotFoundException("Producer profile not found for locationId: " + producerLocationId));
  }

  public List<TruncatedProducerResponse> getProfiles(
      UUID lineOfBusinessId, Boolean mostRecent, Integer maxLimit) {
      log.info(
        "Searching for producers with lineOfBusinessId: {}, limit: {}", lineOfBusinessId, maxLimit);

      lineOfBusinessContract.findLineOfBusiness(lineOfBusinessId);

      var results =
        searchRepository.findMostRecentProfiles(lineOfBusinessId, Limit.of(maxLimit));
    
    log.info("Found {} producers for lineOfBusinessId: {}", results.size(), lineOfBusinessId);
    
    return producerSearchMapper.limitedOutputResponse(results);
  }
}
