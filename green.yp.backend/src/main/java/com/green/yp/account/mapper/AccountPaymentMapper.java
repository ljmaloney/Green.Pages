package com.green.yp.account.mapper;

import com.green.yp.api.apitype.invoice.InvoiceResponse;
import com.green.yp.api.apitype.payment.*;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AccountPaymentMapper {

  @Mapping(target = "referenceId", source = "referenceId")
  @Mapping(target = "payorAddress1", source = "addressLine1")
  @Mapping(target = "payorAddress2", source = "addressLine2")
  @Mapping(target = "payorCity", source = "city")
  @Mapping(target = "payorState", source = "state")
  @Mapping(target = "payorPostalCode", source = "postalCode")
  PaymentMethodRequest toPaymentMethod(@NotNull @NonNull ApiPaymentRequest paymentMethod);

  @Mapping(target = "referenceId", source = "paymentRequest.referenceId")
  @Mapping(target = "paymentToken", source = "savedCustomerCard.cardRef")
  @Mapping(target = "paymentAmount", source = "invoice.invoiceTotal")
  @Mapping(target = "totalAmount", source = "invoice.invoiceTotal")
  @Mapping(target = "note", source = "invoice.invoiceNumber")
  @Mapping(target = "address", source = "paymentRequest.addressLine1")
  @Mapping(target = "city", source = "paymentRequest.city")
  @Mapping(target = "state", source = "paymentRequest.state")
  @Mapping(target = "postalCode", source = "paymentRequest.postalCode")
  @Mapping(target = "phoneNumber", source = "paymentRequest.phoneNumber")
  @Mapping(target = "emailAddress", source = "paymentRequest.emailAddress")
  PaymentRequest toPaymentRequest(
      @NotNull @NonNull ApiPaymentRequest paymentRequest,
      PaymentMethodResponse savedCustomerCard,
      InvoiceResponse invoice);

  ApiPaymentResponse toApiResponse(PaymentMethodResponse paymentMethodResponse);
}
