package com.green.yp.payment.mapper;

import com.green.yp.api.apitype.payment.ApplyPaymentMethodRequest;
import com.green.yp.api.apitype.payment.PaymentMethodRequest;
import com.green.yp.api.apitype.payment.PaymentMethodResponse;
import com.green.yp.api.apitype.payment.PaymentResponse;
import com.green.yp.payment.data.model.PaymentMethod;
import com.green.yp.payment.data.model.PaymentTransaction;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PaymentMapper {
  @Mapping(source = "savedTransaction.id", target = "transactionId")
  @Mapping(source = "savedTransaction.invoiceId", target = "producerInvoiceId")
  @Mapping(source = "savedTransaction.paymentMethodId", target = "methodId")
  PaymentResponse fromTransaction(PaymentTransaction savedTransaction);

  PaymentMethodRequest toPaymentRequest(ApplyPaymentMethodRequest paymentRequest);

  PaymentMethod toEntity(PaymentMethodRequest paymentRequest);

  @Mapping(source = "paymentMethod.id", target = "paymentMethodId")
  PaymentMethodResponse fromEntity(PaymentMethod paymentMethod);
}
