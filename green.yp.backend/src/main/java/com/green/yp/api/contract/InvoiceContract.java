package com.green.yp.api.contract;

import com.green.yp.api.apitype.invoice.InvoiceResponse;
import java.util.Optional;
import java.util.UUID;

import com.green.yp.producer.invoice.service.InvoiceService;
import org.springframework.stereotype.Component;

@Component
public class InvoiceContract {
  final InvoiceService invoiceService;

  public InvoiceContract(InvoiceService invoiceService) {
    this.invoiceService = invoiceService;
  }

  public InvoiceResponse findInvoice(UUID invoiceId, String requestIP) {
    return invoiceService.findInvoice(invoiceId, requestIP);
  }

  public InvoiceResponse markInvoicePaid(UUID invoiceId, String requestIP) {
    return invoiceService.markInvoicePaid(invoiceId, Optional.empty(), requestIP);
  }

  public InvoiceResponse createInvoice(UUID accountId, String requestIP) {
    return invoiceService.createInvoice(accountId, requestIP);
  }
}
