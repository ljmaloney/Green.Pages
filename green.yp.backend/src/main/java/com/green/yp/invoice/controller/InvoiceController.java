package com.green.yp.invoice.controller;

import com.green.yp.api.apitype.invoice.InvoiceResponse;
import com.green.yp.api.apitype.invoice.InvoiceType;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.config.security.AuthUser;
import com.green.yp.config.security.AuthenticatedUser;
import com.green.yp.invoice.service.InvoiceService;
import com.green.yp.security.IsAdmin;
import com.green.yp.security.IsSubscriberAdminOrAdmin;
import com.green.yp.util.DateUtils;
import com.green.yp.util.RequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
@Validated
@Tag(name = "Endpoint for retrieval of invoices")
@RequestMapping("invoice")
public class InvoiceController {
    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Operation(summary = "Returns the list of invoices for a given date range")
    @ApiResponse(responseCode = "200", description = "Producer/Subscriber contact")
    @ApiResponse(responseCode = "404", description = "Contact not found")
    @IsSubscriberAdminOrAdmin
    @GetMapping(path = "producer/{producerId}/search", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<List<InvoiceResponse>> findProducerInvoices(@PathVariable("producerId") String producerId,
                                                                   @RequestParam("startDate") String startDate,
                                                                   @RequestParam("endDate") String endDate,
                                                                   @AuthUser AuthenticatedUser authenticatedUser,
                                                                   HttpServletRequest request) throws Exception {
        return new ResponseApi<>(invoiceService.findInvoices(InvoiceType.SUBSCRIPTION, producerId,
                DateUtils.parseDate(startDate, LocalDate.class),
                DateUtils.parseDate(endDate, LocalDate.class), authenticatedUser, RequestUtil.getRequestIP(request)), null);
    }

    @Operation(summary = "Returns the list of invoices for classifieds")
    @ApiResponse(responseCode = "200", description = "Producer/Subscriber contact")
    @ApiResponse(responseCode = "404", description = "Contact not found")
    @IsAdmin
    @GetMapping(path = "classifieds/search", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<List<InvoiceResponse>> findClassifiedInvoices(@RequestParam("startDate") String startDate,
                                                                        @RequestParam("endDate") String endDate,
                                                                      @AuthUser AuthenticatedUser authenticatedUser,
                                                                      HttpServletRequest request) {
        return new ResponseApi<>(invoiceService.findInvoices(InvoiceType.CLASSIFIED, null,
                DateUtils.parseDate(startDate, LocalDate.class),
                DateUtils.parseDate(endDate, LocalDate.class),
                authenticatedUser, RequestUtil.getRequestIP(request)), null);
    }
}
