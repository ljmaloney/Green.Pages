package com.green.yp.classifieds.mapper;

import com.green.yp.api.apitype.payment.ApiPaymentRequest;
import com.green.yp.api.apitype.payment.PaymentRequest;
import java.math.BigDecimal;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ClassifiedPaymentMapper {

    @Mapping(target = "referenceId", source = "request.referenceId")
    @Mapping(target = "city", source="request.city")
    @Mapping(target = "state", source = "request.state")
    @Mapping(target = "postalCode", source = "request.postalCode")
    @Mapping(target = "emailAddress", source = "request.emailAddress")
    @Mapping(target = "phoneNumber", source = "request.phoneNumber")
    @Mapping(target = "address", source = "request.addressLine1")
    PaymentRequest toPaymentRequest(ApiPaymentRequest request,
                                    String statementDescription,
                                    BigDecimal paymentAmount,
                                    BigDecimal totalAmount,
                                    String note,
                                    String ipAddress);

}
