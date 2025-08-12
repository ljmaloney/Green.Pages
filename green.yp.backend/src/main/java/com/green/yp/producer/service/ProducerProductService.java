package com.green.yp.producer.service;

import com.green.yp.api.AuditRequest;
import com.green.yp.api.apitype.enumeration.AuditActionType;
import com.green.yp.api.apitype.enumeration.AuditObjectType;
import com.green.yp.api.apitype.producer.CreateProductRequest;
import com.green.yp.api.apitype.producer.DiscontinueProductRequest;
import com.green.yp.api.apitype.producer.ProducerProductRequest;
import com.green.yp.api.apitype.producer.ProducerProductResponse;
import com.green.yp.exception.NotFoundException;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.producer.data.model.Producer;
import com.green.yp.producer.data.model.ProducerProduct;
import com.green.yp.producer.data.repository.ProducerProductRepository;
import com.green.yp.producer.mapper.ProducerProductMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProducerProductService {

  public static final String PRODUCER_PRODUCT_ENTITY = "ProducerProduct";
  private final ProducerOrchestrationService producerService;

  private final ProducerLocationService locationService;

  private final ProducerSearchService searchService;

  private final ProducerProductRepository productRepository;

  private final ProducerProductMapper mapper;

  public ProducerProductService(
          ProducerOrchestrationService producerService,
          ProducerLocationService locationService, ProducerSearchService searchService,
          ProducerProductRepository productRepository,
          ProducerProductMapper mapper) {
    this.producerService = producerService;
    this.locationService = locationService;
      this.searchService = searchService;
      this.productRepository = productRepository;
    this.mapper = mapper;
  }

  public List<ProducerProductResponse> findAllProducts(@NotNull @NonNull UUID producerId,
                                                       @NotNull @NonNull UUID locationId) {
    log.info("Loading all products for location {}", locationId);
    return mapper.fromEntity(productRepository.findAllByProducerLocationId(locationId));
  }

  public ProducerProductResponse findProduct(@NotNull @NonNull UUID productId) {
    log.info("Loading product details for {}", productId);
    return productRepository
        .findById(productId)
        .map(mapper::fromEntity)
        .orElseThrow(() -> new NotFoundException(PRODUCER_PRODUCT_ENTITY, productId));
  }

  @AuditRequest(
      requestParameter = "productRequest",
      objectType = AuditObjectType.PRODUCER_PRODUCT,
      actionType = AuditActionType.CREATE)
  public ProducerProductResponse createProduct(
      @NotNull @NonNull @Valid CreateProductRequest productRequest,
      String userId,
      String requestIP) {
    log.info(
        "Adding a new product {} to producer {} and location {}",
        productRequest.name(),
        productRequest.producerId(),
        productRequest.producerLocationId());

    validateProducerActive(productRequest.producerId(), productRequest.producerLocationId());
    var response = mapper.fromEntity(productRepository.saveAndFlush(mapper.toEntity(productRequest)));
    searchService.upsertProduct(response);
    return response;
  }

  @AuditRequest(
      requestParameter = "productRequest",
      objectType = AuditObjectType.PRODUCER_PRODUCT,
      actionType = AuditActionType.UPDATE)
  public ProducerProductResponse updateProduct(
      @NotNull @NonNull ProducerProductRequest productRequest, String userId, String requestIP) {

    log.info("Updating product {} - {}", productRequest.productId(), productRequest.name());

    var product =
        productRepository
            .findById(productRequest.productId())
            .orElseThrow(
                () -> new NotFoundException(PRODUCER_PRODUCT_ENTITY, productRequest.productId()));

    validateProducerActive(product.getProducerId(), product.getProducerLocationId());

    mapper.updateEntity(productRequest, product);

    if (Boolean.TRUE.equals(productRequest.discontinued())) {
      product.setDiscontinueDate(productRequest.discontinueDate());
      product.setLastOrderDate(productRequest.lastOrderDate());
    }

      var response = mapper.fromEntity(productRepository.saveAndFlush(product));
      searchService.upsertProduct(response);
      return response;
  }

  public void discontinueImmediate(
      @NotNull @NonNull UUID productId, String userId, String requestIP) {
    discontinue(
        new DiscontinueProductRequest(productId, LocalDate.now(), LocalDate.now()),
        userId,
        requestIP);
  }

  @AuditRequest(
      requestParameter = "discontinueRequest",
      objectType = AuditObjectType.PRODUCER_PRODUCT,
      actionType = AuditActionType.DISCONTINUE_PRODUCT)
  public ProducerProductResponse discontinue(
      @Valid DiscontinueProductRequest discontinueRequest, String userId, String requestIP) {

    log.info("Discontinue product : {}", discontinueRequest);

    final ProducerProduct product =
        productRepository
            .findById(discontinueRequest.productId())
            .orElseThrow(
                () -> new NotFoundException(PRODUCER_PRODUCT_ENTITY, discontinueRequest.productId()));

    if (product.isDiscontinued() && !discontinueDatesChanged(product, discontinueRequest)) {
      return mapper.fromEntity(product);
    }

    validateProducerActive(product.getProducerId(), product.getProducerLocationId());

    product.setDiscontinued(true);
    product.setDiscontinueDate(discontinueRequest.discontinueDate());
    product.setLastOrderDate(discontinueRequest.lastOrderDate());

    return mapper.fromEntity(productRepository.saveAndFlush(product));
  }

  private boolean discontinueDatesChanged(
      ProducerProduct product, DiscontinueProductRequest discontinueRequest) {
    if (product.getDiscontinueDate() == null && product.getLastOrderDate() == null) {
      return true;
    }
    return !(product.getDiscontinueDate().equals(discontinueRequest.discontinueDate())
        && product.getLastOrderDate().equals(discontinueRequest.lastOrderDate()));
  }

  private void validateProducerActive(UUID producerId, UUID producerLocationId) {
    Producer producer = producerService.findActiveProducer(producerId);

    if (producer.getCancelDate() != null) {
      log.warn(
              "ProducerAccount {} is being cancelled as of {}, cannot create/update product",
              producer.getId(),
              producer.getCancelDate());
      throw new PreconditionFailedException(
              "ProducerAccount %s is being cancelled as of %s, cannot create/update products",
              producer.getId(), producer.getCancelDate());
    }

    locationService.findActiveLocation(producerLocationId);
  }
}
