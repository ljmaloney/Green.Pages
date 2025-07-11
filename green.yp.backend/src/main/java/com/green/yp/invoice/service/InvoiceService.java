package com.green.yp.invoice.service;

import com.green.yp.api.apitype.invoice.InvoiceRequest;
import com.green.yp.api.apitype.invoice.InvoiceResponse;
import com.green.yp.invoice.data.model.Invoice;
import com.green.yp.invoice.data.repository.InvoiceRepository;
import com.green.yp.invoice.mapper.InvoiceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper mapper;

    public InvoiceService(InvoiceRepository invoiceRepository, InvoiceMapper mapper){
        this.invoiceRepository = invoiceRepository;
        this.mapper = mapper;
    }

    public InvoiceResponse createInvoice(InvoiceRequest invoiceRequest) {

        Invoice invoice = mapper.toEntity(invoiceRequest);

        var nextInvoiceNumber = getNextInvoiceNumber();
        invoice.setInvoiceNumber(nextInvoiceNumber);

        return mapper.fromEntity(invoiceRepository.saveAndFlush(invoice));
    }

    private String getNextInvoiceNumber() {
        OffsetDateTime startDate = OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS);
        OffsetDateTime endDate = startDate.plusDays(1).minusSeconds(1);

        Integer counter = invoiceRepository.getCountByDate(startDate, endDate);

        String prefix = new SimpleDateFormat("yyyyMMdd").format(Date.from(startDate.toInstant()));
        return String.format(
                "%s-%s", prefix, new DecimalFormat("000000").format(counter.doubleValue() + 1));
    }
}
