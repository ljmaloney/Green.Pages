package com.green.yp.account.mapper;

import com.green.yp.api.apitype.invoice.InvoiceResponse;
import com.green.yp.api.apitype.payment.ApplyPaymentMethodRequest;
import com.green.yp.api.apitype.payment.PaymentMethodRequest;
import com.green.yp.api.apitype.payment.PaymentMethodResponse;
import com.green.yp.api.apitype.payment.PaymentRequest;
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

  @Mapping(target="referenceId", source="producerId")
  PaymentMethodRequest toPaymentMethod(ApplyPaymentMethodRequest paymentMethod);

    @Mapping(target="paymentToken", source="savedCustomerCard.cardRef")
    @Mapping(target = "paymentAmount", source="invoice.invoiceTotal")
    @Mapping(target = "totalAmount", source="invoice.invoiceTotal")
    @Mapping(target = "note", source="invoice.invoiceNumber")
    @Mapping(target="address", source ="paymentRequest.payorAddress1")
    @Mapping(target="city", source ="paymentRequest.payorCity")
    @Mapping(target="state", source ="paymentRequest.payorState")
    @Mapping(target="postalCode", source ="paymentRequest.payorPostalCode")
    @Mapping(target="phoneNumber", source = "paymentRequest.phoneNumber")
    @Mapping(target="emailAddress", source = "paymentRequest.emailAddress")
    PaymentRequest toPaymentRequest(@NotNull @NonNull ApplyPaymentMethodRequest paymentRequest,
                                    PaymentMethodResponse savedCustomerCard,
                                    InvoiceResponse invoice);
}
