package com.green.yp.payment.mapper;

import com.green.yp.api.apitype.payment.ApplyPaymentMethodRequest;
import com.green.yp.api.apitype.payment.PaymentMethodRequest;
import com.green.yp.api.apitype.payment.PaymentMethodResponse;
import com.green.yp.api.apitype.payment.PaymentResponse;
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
  PaymentResponse fromTransaction(ProducerPaymentTransaction savedTransaction);

  PaymentMethodRequest toPaymentRequest(ApplyPaymentMethodRequest paymentRequest);

  ProducerPaymentMethod toEntity(PaymentMethodRequest paymentRequest);

  @Mapping(source = "id", target = "paymentMethodId")
  PaymentMethodResponse fromEntity(ProducerPaymentMethod producerPaymentMethod);
}
