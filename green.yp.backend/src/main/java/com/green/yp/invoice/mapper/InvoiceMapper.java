package com.green.yp.invoice.mapper;

import com.green.yp.api.apitype.invoice.InvoiceLineItemResponse;
import com.green.yp.api.apitype.invoice.InvoiceResponse;
import com.green.yp.invoice.data.model.Invoice;
import com.green.yp.invoice.data.model.InvoiceLineItem;
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

  List<InvoiceResponse> fromEntityList(List<Invoice> invoice);

  @Mapping(source = "invoice.id", target = "invoiceId")
  @Mapping(source = "invoice.printedInvoiceId", target = "invoiceNumber")
  InvoiceResponse fromEntity(Invoice invoice);

  @Mapping(source = "lineItem.id", target = "lineItemId")
  InvoiceLineItemResponse fromEntity(InvoiceLineItem lineItem);

  @Mapping( target = "subscriptionName", source = "subscription.displayName")
  @Mapping(target = "createDate", source = "invoice.createDate")
  @Mapping( target= "subscriptionId", source="invoice.subscriptionId")
  InvoiceResponse fromEntity(Invoice invoice, SubscriptionDto subscription);
}
