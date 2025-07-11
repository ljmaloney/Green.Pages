package com.green.yp.producer.invoice.mapper;

import com.green.yp.api.apitype.invoice.InvoiceLineItemResponse;
import com.green.yp.api.apitype.invoice.InvoiceResponse;
import com.green.yp.producer.invoice.data.model.ProducerInvoice;
import com.green.yp.producer.invoice.data.model.ProducerInvoiceLineItem;
import com.green.yp.reference.dto.SubscriptionDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface InvoiceMapper {

  List<InvoiceResponse> fromEntityList(List<ProducerInvoice> producerInvoice);

  @Mapping(source = "id", target = "invoiceId")
  @Mapping(source = "printedInvoiceId", target = "invoiceNumber")
  InvoiceResponse fromEntity(ProducerInvoice producerInvoice);

  @Mapping(source = "id", target = "lineItemId")
  InvoiceLineItemResponse fromEntity(ProducerInvoiceLineItem lineItem);

  @Mapping( target = "subscriptionName", source = "subscription.displayName")
  @Mapping(target = "createDate", source = "invoice.createDate")
  @Mapping( target= "subscriptionId", source="invoice.subscriptionId")
  InvoiceResponse fromEntity(ProducerInvoice invoice, SubscriptionDto subscription);
}
