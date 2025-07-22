package com.green.yp.payment.controller;

import com.green.yp.api.apitype.payment.PaymentMethodRequest;
import com.green.yp.api.apitype.payment.PaymentMethodResponse;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.payment.data.repository.PaymentMethodRepository;
import com.green.yp.payment.service.PaymentMethodService;
import com.green.yp.payment.service.PaymentOrchestrationService;
import com.green.yp.security.IsSubscriberAdminOrAdmin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Validated
@Tag(name = "Endpoints for retrieving payment method / updating payment method")
@RequestMapping("payment")
public class PaymentController {

    private final PaymentMethodService methodService;
    private final PaymentOrchestrationService orchestrationService;

    public PaymentController(PaymentMethodService methodService, PaymentOrchestrationService orchestrationService) {
        this.methodService = methodService;
        this.orchestrationService = orchestrationService;
    }

    @Operation(summary = "Returns the active payment method for the producer")
    @ApiResponse(responseCode = "404", description = "Payment method not found")
    @IsSubscriberAdminOrAdmin
    @GetMapping(path="producer/{producerId}", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<PaymentMethodResponse> getCurrentMethod(@PathVariable("producerId") String producerId) {
        return new ResponseApi<>(methodService.findActiveMethod(producerId), null);
    }

    @IsSubscriberAdminOrAdmin
    @PostMapping(path="replace", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<PaymentMethodResponse> replacePaymentMethod(@RequestBody PaymentMethodRequest methodRequest) {
        return new ResponseApi<>(orchestrationService.replaceCardOnFile(methodRequest), null);
    }
}
