package com.green.yp.payment.controller;

import com.green.yp.api.apitype.payment.ApiPaymentRequest;
import com.green.yp.api.apitype.payment.PaymentMethodRequest;
import com.green.yp.api.apitype.payment.PaymentMethodResponse;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.config.security.AuthUser;
import com.green.yp.config.security.AuthenticatedUser;
import com.green.yp.payment.service.PaymentMethodService;
import com.green.yp.payment.service.PaymentOrchestrationService;
import com.green.yp.security.IsSubscriberAdminOrAdmin;
import com.green.yp.util.RequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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

  public PaymentController(
      PaymentMethodService methodService, PaymentOrchestrationService orchestrationService) {
    this.methodService = methodService;
    this.orchestrationService = orchestrationService;
  }

  @Operation(summary = "Returns the active payment method for the producer")
  @ApiResponse(responseCode = "404", description = "Payment method not found")
  @IsSubscriberAdminOrAdmin
  @GetMapping(path = "producer/{producerId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<PaymentMethodResponse> getCurrentMethod(
      @PathVariable("producerId") String producerId) {
    return new ResponseApi<>(methodService.findActiveMethod(producerId), null);
  }

  @IsSubscriberAdminOrAdmin
  @PostMapping(
      path = "replace",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<PaymentMethodResponse> replacePaymentMethod(@Parameter(hidden = true) @AuthUser AuthenticatedUser authenticatedUser,
                                                                 @RequestParam(name = "createNew", defaultValue = "false") boolean createNew,
                                                                 @RequestBody ApiPaymentRequest methodRequest, HttpServletRequest request) {
    return new ResponseApi<>(orchestrationService.replaceCardOnFile(methodRequest,  authenticatedUser, createNew, RequestUtil.getRequestIP(request)), null);
  }
}
