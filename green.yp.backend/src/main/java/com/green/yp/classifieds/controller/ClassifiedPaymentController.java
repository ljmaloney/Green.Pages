package com.green.yp.classifieds.controller;

import com.green.yp.api.apitype.classified.ClassifiedPaymentResponse;
import com.green.yp.api.apitype.payment.ApiPaymentRequest;
import com.green.yp.classifieds.service.ClassifiedPaymentService;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.util.RequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Validated
@RequestMapping("classified")
@Tag(name = "REST endpoint to process payment")
public class ClassifiedPaymentController {

    private final ClassifiedPaymentService paymentService;

    public ClassifiedPaymentController(ClassifiedPaymentService paymentService){
        this.paymentService = paymentService;
    }

    @Operation(summary = "Proceses payment for a classified ad")
    @PostMapping(path = "payment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseApi<ClassifiedPaymentResponse> processPayment(@RequestBody @Valid ApiPaymentRequest paymentRequest,
                                                                 HttpServletRequest httpServletRequest){
        return new ResponseApi<>(paymentService.processPayment(paymentRequest,
                RequestUtil.getRequestIP(httpServletRequest)), null);
    }

}
