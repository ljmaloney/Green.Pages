package com.green.yp.invoice.controller;

import com.green.yp.api.apitype.invoice.InvoiceResponse;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.invoice.service.InvoiceService;
import com.green.yp.security.IsAnyAuthenticatedUser;
import com.green.yp.util.DateUtils;
import com.green.yp.util.RequestUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeEditor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@Validated
@Tag(name = "For retrieval of subscriber invoices")
@RequestMapping("invoice")
public class InvoiceController {
    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService){
        this.invoiceService = invoiceService;
    }

    @IsAnyAuthenticatedUser
    @GetMapping(path="{invoiceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<InvoiceResponse> findInvoice(@PathVariable("invoiceId") UUID invoiceId){
        return new ResponseApi<>(invoiceService.findInvoice(invoiceId, RequestUtil.getRequestIP()), null);
    }

    @IsAnyAuthenticatedUser
    @GetMapping(path="/producer/{producerId}/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<List<InvoiceResponse>> findInvoices(@PathVariable("producerId") UUID producerId,
                                                           @RequestParam("startDate") String startDate,
                                                           @RequestParam("endDate") String endDate,
                                                           @RequestParam(value = "descending", defaultValue = "true") Boolean descending){
        return new ResponseApi<>(invoiceService.findInvoices(producerId,
                DateUtils.parseDate(startDate, LocalDate.class), DateUtils.parseDate(endDate, LocalDate.class), descending), null);
    }
}
