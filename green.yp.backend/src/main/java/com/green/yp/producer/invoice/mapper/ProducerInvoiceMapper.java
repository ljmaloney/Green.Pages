package com.green.yp.producer.invoice.mapper;

import com.green.yp.api.apitype.invoice.ProducerInvoiceLineItemResponse;
import com.green.yp.api.apitype.invoice.ProducerInvoiceResponse;
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
public interface ProducerInvoiceMapper {

  List<ProducerInvoiceResponse> fromEntityList(List<ProducerInvoice> producerInvoice);

  @Mapping(source = "id", target = "invoiceId")
  @Mapping(source = "printedInvoiceId", target = "invoiceNumber")
  ProducerInvoiceResponse fromEntity(ProducerInvoice producerInvoice);

  @Mapping(source = "id", target = "lineItemId")
  ProducerInvoiceLineItemResponse fromEntity(ProducerInvoiceLineItem lineItem);

  @Mapping( target = "subscriptionName", source = "subscription.displayName")
  @Mapping(target = "createDate", source = "invoice.createDate")
  @Mapping( target= "subscriptionId", source="invoice.subscriptionId")
  ProducerInvoiceResponse fromEntity(ProducerInvoice invoice, SubscriptionDto subscription);
}
