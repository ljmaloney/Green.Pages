package com.green.yp.payment.mapper;

import com.green.yp.api.apitype.payment.*;
import com.green.yp.payment.data.model.ProducerPaymentMethod;
import com.green.yp.payment.data.model.ProducerPaymentTransaction;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PaymentMapper {
  @Mapping(source = "id", target = "transactionId")
  @Mapping(source = "invoiceId", target = "producerInvoiceId")
  @Mapping(source = "paymentMethodId", target = "methodId")
  ProducerPaymentResponse fromTransaction(ProducerPaymentTransaction savedTransaction);

  ProducerPaymentMethodRequest toPaymentRequest(ApplyPaymentMethodRequest paymentRequest);

  ProducerPaymentMethod toEntity(ProducerPaymentMethodRequest paymentRequest);

  @Mapping(source = "id", target = "paymentMethodId")
  ProducerPaymentMethodResponse fromEntity(ProducerPaymentMethod producerPaymentMethod);

  @Mapping(target = "referenceId", source="referenceId")
  @Mapping(target = "payorAddress1", source = "addressLine1")
  @Mapping(target = "payorAddress2", source = "addressLine2")
  @Mapping(target = "payorCity", source = "city")
  @Mapping(target = "payorState", source = "state")
  @Mapping(target = "payorPostalCode", source = "postalCode")
  PaymentMethodRequest toPaymentMethodRequest(ApiPaymentRequest apiPaymentRequest);
}
