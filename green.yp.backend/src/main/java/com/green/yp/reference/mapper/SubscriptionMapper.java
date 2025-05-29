package com.green.yp.reference.mapper;

import com.green.yp.api.apitype.CreateSubscriptionRequest;
import com.green.yp.reference.data.model.Subscription;
import com.green.yp.reference.data.model.SubscriptionFeature;
import com.green.yp.reference.dto.SubscriptionDto;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SubscriptionMapper {
  List<SubscriptionDto> mapToDto(List<Subscription> subscriptionList);

  @Mapping(source = "subscription.id", target = "subscriptionId")
  @Mapping(source = "subscription.features", target = "features")
  SubscriptionDto mapToDto(Subscription subscription);

  List<String> toFeatureStringList(List<SubscriptionFeature> features);

  default String mapFeatureToString(SubscriptionFeature feature) {
    return feature.getFeatureName();
  }

  Subscription mapToEntity(CreateSubscriptionRequest createRequest);

  default List<SubscriptionFeature> mapToEntity(List<String> features) {
    return features.stream()
        .map(s -> SubscriptionFeature.builder().featureName(s).build())
        .toList();
  }
}
