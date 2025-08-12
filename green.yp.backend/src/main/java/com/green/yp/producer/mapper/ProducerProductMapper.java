package com.green.yp.producer.mapper;

import com.green.yp.api.apitype.producer.CreateProductRequest;
import com.green.yp.api.apitype.producer.ProducerProductRequest;
import com.green.yp.api.apitype.producer.ProducerProductResponse;
import com.green.yp.producer.data.model.ProducerProduct;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProducerProductMapper {
  List<ProducerProductResponse> fromEntity(List<ProducerProduct> producerProducts);

  @Mapping(source = "product.id", target = "productId")
  ProducerProductResponse fromEntity(ProducerProduct product);

  ProducerProduct toEntity(CreateProductRequest productRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "lastUpdateDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntity(@NotNull @NonNull ProducerProductRequest productRequest,
                      @MappingTarget ProducerProduct product);
}
