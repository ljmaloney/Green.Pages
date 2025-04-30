package com.green.yp.invoice.mapper;

import com.green.yp.api.apitype.invoice.InvoiceLineItemResponse;
import com.green.yp.api.apitype.invoice.InvoiceResponse;
import com.green.yp.invoice.data.model.Invoice;
import com.green.yp.invoice.data.model.InvoiceLineItem;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface InvoiceMapper {
  @Mapping(source = "invoice.id", target = "invoiceId")
  @Mapping(source = "invoice.printedInvoiceId", target = "invoiceNumber")
  InvoiceResponse fromEntity(Invoice invoice);

  @Mapping(source = "lineItem.id", target = "lineItemId")
  InvoiceLineItemResponse fromEntity(InvoiceLineItem lineItem);
}
