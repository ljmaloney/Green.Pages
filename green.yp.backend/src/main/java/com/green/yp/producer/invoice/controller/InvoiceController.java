package com.green.yp.producer.invoice.controller;

import com.green.yp.api.apitype.invoice.ProducerInvoiceResponse;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.producer.invoice.service.ProducerInvoiceService;
import com.green.yp.security.IsAnyAuthenticatedUser;
import com.green.yp.util.DateUtils;
import com.green.yp.util.RequestUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@Validated
@Tag(name = "For retrieval of subscriber invoices")
@RequestMapping("invoice")
public class InvoiceController {
    private final ProducerInvoiceService producerInvoiceService;

    public InvoiceController(ProducerInvoiceService producerInvoiceService){
        this.producerInvoiceService = producerInvoiceService;
    }

    @IsAnyAuthenticatedUser
    @GetMapping(path="{invoiceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<ProducerInvoiceResponse> findInvoice(@PathVariable("invoiceId") UUID invoiceId){
        return new ResponseApi<>(producerInvoiceService.findInvoice(invoiceId, RequestUtil.getRequestIP()), null);
    }

    @IsAnyAuthenticatedUser
    @GetMapping(path="/producer/{producerId}/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<List<ProducerInvoiceResponse>> findInvoices(@PathVariable("producerId") UUID producerId,
                                                                   @RequestParam("startDate") String startDate,
                                                                   @RequestParam("endDate") String endDate,
                                                                   @RequestParam(value = "descending", defaultValue = "true") Boolean descending){
        return new ResponseApi<>(producerInvoiceService.findInvoices(producerId,
                DateUtils.parseDate(startDate, LocalDate.class), DateUtils.parseDate(endDate, LocalDate.class), descending), null);
    }
}
