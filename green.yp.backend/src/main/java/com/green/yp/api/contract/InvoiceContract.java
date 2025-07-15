package com.green.yp.api.contract;

import com.green.yp.api.apitype.invoice.InvoiceRequest;
import com.green.yp.api.apitype.invoice.InvoiceResponse;
import com.green.yp.api.apitype.payment.PaymentTransactionResponse;
import com.green.yp.invoice.service.InvoiceService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class InvoiceContract {
    private final InvoiceService invoiceService;

    public InvoiceContract(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public InvoiceResponse createInvoice(InvoiceRequest invoiceRequest){
        return invoiceService.createInvoice(invoiceRequest);
    }

    public void updatePayment(UUID invoiceId, PaymentTransactionResponse completedPayment) {
        invoiceService.updatePayment(invoiceId, completedPayment);
    }
}
