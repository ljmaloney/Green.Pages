package com.green.yp.reference.mapper;

import com.green.yp.api.apitype.CreateSubscriptionRequest;
import com.green.yp.reference.data.model.Subscription;
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
    SubscriptionDto mapToDto(Subscription subscription);

    //    Subscription mapToEntity(SubscriptionDto subscriptionDto);

    Subscription mapToEntity(CreateSubscriptionRequest createRequest);

}
