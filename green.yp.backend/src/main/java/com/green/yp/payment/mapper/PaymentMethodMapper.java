package com.green.yp.payment.mapper;

import com.green.yp.api.apitype.classified.PaymentMethodResponse;
import com.green.yp.api.apitype.payment.PaymentCustomerResponse;
import com.green.yp.api.apitype.payment.PaymentMethodRequest;
import com.green.yp.api.apitype.payment.PaymentSavedCardResponse;
import com.green.yp.payment.data.model.PaymentMethod;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PaymentMethodMapper {


    @Mapping(target = "id", source = "newCustomer.idempotencyId")
    @Mapping(target = "externCustRef", source="newCustomer.externCustRef")
    @Mapping(target = "cardRef", source="savedCard.cardRef")
    @Mapping(target = "cardDetails", source="savedCard.card")
    PaymentMethod toEntity(PaymentMethodRequest methodRequest,
                           PaymentCustomerResponse newCustomer,
                           PaymentSavedCardResponse savedCard);

    @Mapping(target = "cardRef", source="savedCard.cardRef")
    @Mapping(target = "cardDetails", source="savedCard.card")
    PaymentMethod toEntity(PaymentMethodRequest methodRequest,
                           String externCustRef,
                           PaymentSavedCardResponse savedCard);

    @Mapping(target = "paymentMethodId", source="id")
    PaymentMethodResponse toResponse(PaymentMethod paymentMethod);
}
