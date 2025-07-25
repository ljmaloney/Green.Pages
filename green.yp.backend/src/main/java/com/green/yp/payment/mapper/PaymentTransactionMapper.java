package com.green.yp.payment.mapper;

import com.green.yp.api.apitype.payment.PaymentRequest;
import com.green.yp.api.apitype.payment.PaymentTransactionResponse;
import com.green.yp.payment.data.model.PaymentTransaction;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PaymentTransactionMapper {

  @Mapping(target = "address", source = "address")
  PaymentTransaction toEntity(PaymentRequest paymentRequest);

  @Mapping(target = "transactionId", source = "id")
  PaymentTransactionResponse fromEntity(PaymentTransaction save);
}
