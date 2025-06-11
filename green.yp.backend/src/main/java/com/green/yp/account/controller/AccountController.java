package com.green.yp.account.controller;

import com.green.yp.account.service.AccountPaymentService;
import com.green.yp.account.service.AccountService;
import com.green.yp.api.apitype.account.AccountResponse;
import com.green.yp.api.apitype.account.CreateAccountRequest;
import com.green.yp.api.apitype.account.UpdateAccountRequest;
import com.green.yp.api.apitype.payment.ApiPaymentResponse;
import com.green.yp.api.apitype.payment.ApplyPaymentMethodRequest;
import com.green.yp.api.apitype.payment.ApplyPaymentRequest;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.exception.SystemException;
import com.green.yp.util.RequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Validated
@RequestMapping("account")
@Tag(name = "Orchestration Service supporting creation of subscriber / producer account")
public class AccountController {

  private final AccountService accountService;
  private final AccountPaymentService paymentService;

  public AccountController(AccountService accountService, AccountPaymentService paymentService) {
    this.accountService = accountService;
    this.paymentService = paymentService;
  }

  @ApiResponse(
      responseCode = org.apache.hc.core5.http.HttpStatus.SC_OK + "",
      description = "Returns the requested account information",
      content = @Content(mediaType = "application/json"))
  @GetMapping(path = "/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<AccountResponse> findAccount(
      @PathVariable(name = "accountId") String accountId) {
    return new ResponseApi<>(accountService.findAccount(UUID.fromString(accountId)), null);
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Creates the subscriber/producer account")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseApi<AccountResponse> createAccount(
      @RequestBody @Valid CreateAccountRequest account) throws NoSuchAlgorithmException {
    return new ResponseApi<>(
        accountService.createAccount(account, RequestUtil.getRequestIP()), null);
  }

  @Operation(
      summary = "Applies the initial subscription payment",
      description = "Creates an invoice and applies the initial payment for a new subscription")
  @PostMapping(
      path = "/applyInitialPayment",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<ApiPaymentResponse> applyInitialPayment(
      @RequestBody @Valid ApplyPaymentMethodRequest paymentRequest) {
    return new ResponseApi<>(
        paymentService.applyInitialPayment(paymentRequest, RequestUtil.getRequestIP()), null);
  }

  @Operation(summary = "Applies a payment on an existing subscription with an invoice")
  @PostMapping(
      path = "/applyPayment",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<ApiPaymentResponse> applyPayment(
      @RequestBody @Valid ApplyPaymentRequest paymentRequest) {
    return new ResponseApi<>(
        paymentService.applyPayment(paymentRequest, null, RequestUtil.getRequestIP()), null);
  }

  @Operation(summary = "Update the subscriber / producer account records")
  @PutMapping(
      name = "/account",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseApi<AccountResponse> updateAccount(
      @RequestBody @Valid UpdateAccountRequest account) {
    try {
      return new ResponseApi<>(
          accountService.updateAccount(Optional.empty(), account, RequestUtil.getRequestIP()),
          null);
    } catch (NoSuchAlgorithmException e) {
      throw new SystemException("System error, missing configuration", e);
    }
  }

  @Operation(
      summary = "Removes / disables unpaid accounts",
      description =
          "Removes or disables unpaid accounts where a payment has not been received in over specified number of days")
  @PostMapping(path = "/clean/unpaid", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseApi<String> cleanUnpaidAccounts(
      @RequestParam(name = "days", defaultValue = "30", required = false) Integer daysOld) {
    return new ResponseApi<>(
        paymentService.cleanUnpaidAccounts(daysOld, RequestUtil.getRequestIP()), null);
  }

  @Operation(summary = "Cancels or disables an account")
  @DeleteMapping(path = "/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseApi<String> cancelAccount(@PathVariable UUID accountId) {
    return new ResponseApi<>(
        accountService.cancelAccount(accountId, RequestUtil.getRequestIP()), null);
  }
}
