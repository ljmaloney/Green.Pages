package com.green.yp.api.contract;

import com.green.yp.api.apitype.invoice.ProducerInvoiceResponse;
import java.util.Optional;
import java.util.UUID;

import com.green.yp.producer.invoice.service.InvoiceService;
import org.springframework.stereotype.Component;

@Component
public class ProducerInvoiceContract {
  final InvoiceService invoiceService;

  public ProducerInvoiceContract(InvoiceService invoiceService) {
    this.invoiceService = invoiceService;
  }

  public ProducerInvoiceResponse findInvoice(UUID invoiceId, String requestIP) {
    return invoiceService.findInvoice(invoiceId, requestIP);
  }

  public ProducerInvoiceResponse markInvoicePaid(UUID invoiceId, String requestIP) {
    return invoiceService.markInvoicePaid(invoiceId, Optional.empty(), requestIP);
  }

  public ProducerInvoiceResponse createInvoice(UUID accountId, String requestIP) {
    return invoiceService.createInvoice(accountId, requestIP);
  }
}
