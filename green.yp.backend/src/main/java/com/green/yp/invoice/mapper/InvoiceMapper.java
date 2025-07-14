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
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface InvoiceMapper {
    Invoice toEntity(InvoiceRequest invoiceRequest);
    InvoiceLineItem toEntity(InvoiceLineItemRequest invoiceLineItemRequest);
    List<InvoiceLineItem> toEntity(List<InvoiceLineItemRequest> lineItems);

    InvoiceResponse fromEntity(Invoice invoice);
    InvoiceLineItemResponse fromEntity(InvoiceLineItem lineItem);
    List<InvoiceLineItemResponse> fromEntity(List<InvoiceLineItem> lineItems);

}
