package com.green.yp.api.contract;

import com.green.yp.api.apitype.invoice.InvoiceRequest;
import com.green.yp.api.apitype.invoice.InvoiceResponse;
import com.green.yp.invoice.service.InvoiceService;
import org.springframework.stereotype.Service;

@Service
public class InvoiceContract {
    private final InvoiceService invoiceService;

    public InvoiceContract(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public InvoiceResponse createInvoice(InvoiceRequest invoiceRequest){
        return invoiceService.createInvoice(invoiceRequest);
    }
}
