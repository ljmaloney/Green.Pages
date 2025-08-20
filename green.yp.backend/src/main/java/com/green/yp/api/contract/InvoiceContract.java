package com.green.yp.api.contract;

import com.green.yp.api.apitype.invoice.InvoiceRequest;
import com.green.yp.api.apitype.invoice.InvoiceResponse;
import com.green.yp.api.apitype.payment.PaymentTransactionResponse;
import com.green.yp.config.security.AuthenticatedUser;
import com.green.yp.invoice.service.InvoiceService;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
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

    public void updatePayment(UUID invoiceId, PaymentTransactionResponse completedPayment) {
        invoiceService.updatePayment(invoiceId, completedPayment);
    }

    public Optional<InvoiceResponse> findUnpaidInvoice(@NotNull @NonNull UUID referenceId,
                                                       @NotNull @NonNull AuthenticatedUser authenticatedUser,
                                                       @NotNull @NonNull String requestIP) {
        return invoiceService.findUnpaidInvoice(referenceId, authenticatedUser, requestIP);
    }
}
