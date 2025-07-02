package com.green.yp.classifieds.mapper;

import com.green.yp.api.apitype.classified.ClassifiedAdCustomerResponse;
import com.green.yp.api.apitype.classified.ClassifiedCustomerResponse;
import com.green.yp.api.apitype.classified.ClassifiedResponse;
import com.green.yp.classifieds.data.model.Classified;
import com.green.yp.classifieds.data.model.ClassifiedCustomer;
import com.green.yp.classifieds.data.model.ClassifiedCustomerProjection;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ClassifiedMapper {

  @Mapping(source = "id", target = "customerId")
  ClassifiedCustomerResponse fromEntity(ClassifiedCustomer classifiedCustomer);

  @Mapping(source = "id", target = "classifiedId")
  ClassifiedResponse fromEntity(Classified classified);

  ClassifiedAdCustomerResponse fromProjection(
      ClassifiedCustomerProjection classifiedCustomerProjection);
}
