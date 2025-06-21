package com.green.yp.producer.service;

import com.green.yp.api.AuditRequest;
import com.green.yp.api.apitype.PatchRequest;
import com.green.yp.api.apitype.enumeration.AuditActionType;
import com.green.yp.api.apitype.enumeration.AuditObjectType;
import com.green.yp.api.apitype.producer.CreateProductRequest;
import com.green.yp.api.apitype.producer.DiscontinueProductRequest;
import com.green.yp.api.apitype.producer.ProducerProductRequest;
import com.green.yp.api.apitype.producer.ProducerProductResponse;
import com.green.yp.common.ServiceUtils;
import com.green.yp.exception.BusinessException;
import com.green.yp.exception.NotFoundException;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.producer.data.model.Producer;
import com.green.yp.producer.data.model.ProducerLocation;
import com.green.yp.producer.data.model.ProducerProduct;
import com.green.yp.producer.data.repository.ProducerProductRepository;
import com.green.yp.producer.mapper.ProducerProductMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProducerProductService {

  private final ProducerOrchestrationService producerService;

  private final ProducerLocationService locationService;

  private final ProducerProductRepository productRepository;

  private final ProducerProductMapper mapper;

  public ProducerProductService(
      ProducerOrchestrationService producerService,
      ProducerLocationService locationService,
      ProducerProductRepository productRepository,
      ProducerProductMapper mapper) {
    this.producerService = producerService;
    this.locationService = locationService;
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
        .orElseThrow(() -> new NotFoundException("ProducerProduct", productId));
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

    return updateProduct(
        productRequest.producerId(),
        productRequest.producerLocationId(),
        () -> {
          ProducerProduct product = mapper.toEntity(productRequest);
          return mapper.fromEntity(productRepository.saveAndFlush(product));
        });
  }

  @AuditRequest(
      requestParameter = "productRequest",
      objectType = AuditObjectType.PRODUCER_PRODUCT,
      actionType = AuditActionType.UPDATE)
  public ProducerProductResponse updateProduct(
      @NotNull @NonNull ProducerProductRequest productRequest, String userId, String requestIP) {

    log.info("Updating product {} - {}", productRequest.productId(), productRequest.name());

    final ProducerProduct product =
        productRepository
            .findById(productRequest.productId())
            .orElseThrow(
                () -> new NotFoundException("ProducerProduct", productRequest.productId()));

    return updateProduct(
        product.getProducerId(),
        product.getProducerLocationId(),
        () -> {
          try {
            PropertyUtils.copyProperties(product, productRequest);
            return mapper.fromEntity(productRepository.saveAndFlush(product));
          } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Unexpected error updating product attributes", e);
            throw new BusinessException("Unexpected error updating product attributes", e);
          }
        });
  }

  @AuditRequest(
      requestParameter = "productRequest",
      objectType = AuditObjectType.PRODUCER_PRODUCT,
      actionType = AuditActionType.PATCH)
  public ProducerProductResponse patchProduct(
      @NotNull @NonNull UUID productId,
      @NotNull @NonNull PatchRequest patchRequest,
      String userId,
      String requestIP) {
    log.info("Patching product {}", productId);

    final ProducerProduct product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new NotFoundException("ProducerProduct", productId));

    return updateProduct(
        product.getProducerId(),
        product.getProducerLocationId(),
        () -> {
          ServiceUtils.patchEntity(patchRequest, product);
          return mapper.fromEntity(productRepository.saveAndFlush(product));
        });
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
                () -> new NotFoundException("ProducerProduct", discontinueRequest.productId()));

    if (product.isDiscontinued() && !discontinueDatesChanged(product, discontinueRequest)) {
      return mapper.fromEntity(product);
    }

    return updateProduct(
        product.getProducerId(),
        product.getProducerLocationId(),
        () -> {
          product.setDiscontinued(true);
          product.setDiscontinueDate(discontinueRequest.discontinueDate());
          product.setLastOrderDate(discontinueRequest.lastOrderDate());

          return mapper.fromEntity(productRepository.saveAndFlush(product));
        });
  }

  private boolean discontinueDatesChanged(
      ProducerProduct product, DiscontinueProductRequest discontinueRequest) {
    if (product.getDiscontinueDate() == null && product.getLastOrderDate() == null) {
      return true;
    }
    return !(product.getDiscontinueDate().equals(discontinueRequest.discontinueDate())
        && product.getLastOrderDate().equals(discontinueRequest.lastOrderDate()));
  }

  private ProducerProductResponse updateProduct(
      UUID producerId, UUID producerLocationId, Supplier<ProducerProductResponse> updateSupplier) {
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

    return updateSupplier.get();
  }
}
