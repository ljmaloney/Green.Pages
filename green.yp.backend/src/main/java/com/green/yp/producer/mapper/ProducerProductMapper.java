package com.green.yp.producer.mapper;

import com.green.yp.api.apitype.producer.CreateProductRequest;
import com.green.yp.api.apitype.producer.ProducerProductResponse;
import com.green.yp.producer.data.model.ProducerProduct;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProducerProductMapper {
  List<ProducerProductResponse> fromEntity(List<ProducerProduct> producerProducts);

  @Mapping(source = "product.id", target = "productId")
  ProducerProductResponse fromEntity(ProducerProduct product);

  ProducerProduct toEntity(CreateProductRequest productRequest);
}
