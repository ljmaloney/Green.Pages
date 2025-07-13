package com.green.yp.api.contract;

import com.green.yp.api.apitype.invoice.ProducerInvoiceResponse;
import java.util.Optional;
import java.util.UUID;

import com.green.yp.producer.invoice.service.ProducerInvoiceService;
import org.springframework.stereotype.Component;

@Component
public class ProducerInvoiceContract {
  final ProducerInvoiceService producerInvoiceService;

  public ProducerInvoiceContract(ProducerInvoiceService producerInvoiceService) {
    this.producerInvoiceService = producerInvoiceService;
  }

  public ProducerInvoiceResponse findInvoice(UUID invoiceId, String requestIP) {
    return producerInvoiceService.findInvoice(invoiceId, requestIP);
  }

  public ProducerInvoiceResponse markInvoicePaid(UUID invoiceId, String requestIP) {
    return producerInvoiceService.markInvoicePaid(invoiceId, Optional.empty(), requestIP);
  }

  public ProducerInvoiceResponse createInvoice(UUID accountId, String requestIP) {
    return producerInvoiceService.createInvoice(accountId, requestIP);
  }
}
