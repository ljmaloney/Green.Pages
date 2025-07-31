package com.green.yp.account.mapper;

import com.green.yp.api.apitype.invoice.InvoiceResponse;
import com.green.yp.api.apitype.payment.*;
import com.green.yp.api.apitype.producer.ProducerResponse;
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
  @Mapping(target = "paymentToken", source = "savedCard.cardRef")
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
      PaymentMethodResponse savedCard,
      InvoiceResponse invoice);

  @Mapping(target = "referenceId", source = "producer.producerId")
  @Mapping(target = "paymentToken", source = "savedCard.cardRef")
  @Mapping(target = "paymentAmount", source = "invoice.invoiceTotal")
  @Mapping(target = "totalAmount", source = "invoice.invoiceTotal")
  @Mapping(target = "note", source = "invoice.invoiceNumber")
  @Mapping(target = "firstName", source="savedCard.givenName")
  @Mapping(target = "lastName", source = "savedCard.familyName")
  @Mapping(target = "address", source = "savedCard.payorAddress1")
  @Mapping(target = "city", source = "savedCard.payorCity")
  @Mapping(target = "state", source = "savedCard.payorState")
  @Mapping(target = "postalCode", source = "savedCard.payorPostalCode")
  @Mapping(target = "phoneNumber", source = "savedCard.phoneNumber")
  @Mapping(target = "emailAddress", source = "savedCard.emailAddress")
  PaymentRequest toPaymentRequest(
          @NotNull @NonNull ProducerResponse producer,
          PaymentMethodResponse savedCard,
          InvoiceResponse invoice);

  ApiPaymentResponse toApiResponse(PaymentMethodResponse paymentMethodResponse);
}
