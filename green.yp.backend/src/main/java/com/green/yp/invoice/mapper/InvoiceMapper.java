package com.green.yp.invoice.mapper;

import com.green.yp.api.apitype.invoice.InvoiceLineItemRequest;
import com.green.yp.api.apitype.invoice.InvoiceLineItemResponse;
import com.green.yp.api.apitype.invoice.InvoiceRequest;
import com.green.yp.api.apitype.invoice.InvoiceResponse;
import com.green.yp.invoice.data.model.Invoice;
import com.green.yp.invoice.data.model.InvoiceLineItem;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface InvoiceMapper {
    Invoice toEntity(InvoiceRequest invoiceRequest);
    InvoiceLineItem toEntity(InvoiceLineItemRequest invoiceLineItemRequest);
    List<InvoiceLineItem> toEntity(List<InvoiceLineItemRequest> lineItems);

    @Mapping(target = "invoiceId", source="id")
    InvoiceResponse fromEntity(Invoice invoice);

    @Mapping(target = "lineItemId", source="id")
    InvoiceLineItemResponse fromEntity(InvoiceLineItem lineItem);
    List<InvoiceLineItemResponse> fromEntity(List<InvoiceLineItem> lineItems);

}
