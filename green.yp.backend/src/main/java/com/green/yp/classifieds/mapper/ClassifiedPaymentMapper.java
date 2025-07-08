package com.green.yp.classifieds.mapper;

import com.green.yp.api.apitype.classified.ClassifiedPaymentRequest;
import com.green.yp.api.apitype.payment.PaymentRequest;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ClassifiedPaymentMapper {

    @Mapping(target="city", source="request.city")
    @Mapping(target = "state", source = "request.state")
    @Mapping(target = "postalCode", source = "request.postalCode")
    @Mapping(target = "emailAddress", source = "request.emailAddress")
    @Mapping(target = "phoneNumber", source = "request.phoneNumber")
    PaymentRequest toPaymentRequest(ClassifiedPaymentRequest request,
                                    BigDecimal amount,
                                    BigDecimal totalAmount,
                                    String note,
                                    String ipAddress);

}
