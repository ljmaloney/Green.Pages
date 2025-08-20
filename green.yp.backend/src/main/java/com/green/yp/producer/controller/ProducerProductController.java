package com.green.yp.producer.controller;

import com.green.yp.api.apitype.producer.CreateProductRequest;
import com.green.yp.api.apitype.producer.DiscontinueProductRequest;
import com.green.yp.api.apitype.producer.ProducerProductRequest;
import com.green.yp.api.apitype.producer.ProducerProductResponse;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.producer.service.ProducerProductService;
import com.green.yp.security.IsAnyAuthenticatedUser;
import com.green.yp.util.RequestUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Validated
@RequestMapping("producer")
@Tag(name = "endpoints for managing products offered for sale")
public class ProducerProductController {

  private final ProducerProductService productService;

  public ProducerProductController(ProducerProductService productService) {
    this.productService = productService;
  }

  @GetMapping(path = "/location/{locationId}/products", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<List<ProducerProductResponse>> getAllProducts(
      @NotNull @NonNull @PathVariable("locationId") UUID locationId) {
    return new ResponseApi<>(productService.findAllProducts(UUID.randomUUID(), locationId), null);
  }

  @GetMapping(
      path = "{producerId}/location/{locationId}/products",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<List<ProducerProductResponse>> getAllProducts(
      @NotNull @NonNull @PathVariable("producerId") UUID producerId,
      @NotNull @NonNull @PathVariable("locationId") UUID locationId) {
    return new ResponseApi<>(productService.findAllProducts(producerId, locationId), null);
  }

  @GetMapping(path = "/location/product/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<ProducerProductResponse> findProduct(
      @NotNull @NonNull @PathVariable("productId") UUID productId) {
    return new ResponseApi<>(productService.findProduct(productId), null);
  }

  @IsAnyAuthenticatedUser
  @PostMapping(
      path = "/location/product",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<ProducerProductResponse> createProduct(
      @Valid @RequestBody CreateProductRequest productRequest) {
    return new ResponseApi<>(
        productService.createProduct(productRequest, null, RequestUtil.getRequestIP()), null);
  }

  @IsAnyAuthenticatedUser
  @PutMapping(
      path = "/location/product",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<ProducerProductResponse> updateProduct(
      @Valid @RequestBody ProducerProductRequest productRequest) {
    return new ResponseApi<>(
        productService.updateProduct(productRequest, null, RequestUtil.getRequestIP()), null);
  }

  @IsAnyAuthenticatedUser
  @DeleteMapping(path = "/location/product/{productId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void discontinueImmediate(@NotNull @NonNull @PathVariable("productId") UUID productId) {
    productService.discontinueImmediate(productId, null, RequestUtil.getRequestIP());
  }

  @IsAnyAuthenticatedUser
  @DeleteMapping(
      path = "location/product/discontinue",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<ProducerProductResponse> discontinue(
      @Valid @RequestBody DiscontinueProductRequest discontinueRequest) {
    return new ResponseApi<>(
        productService.discontinue(discontinueRequest, null, RequestUtil.getRequestIP()), null);
  }
}
