package com.green.yp.account.controller;

import com.green.yp.account.service.AccountPaymentService;
import com.green.yp.account.service.AccountService;
import com.green.yp.api.apitype.account.AccountResponse;
import com.green.yp.api.apitype.account.CreateAccountRequest;
import com.green.yp.api.apitype.account.UpdateAccountRequest;
import com.green.yp.api.apitype.payment.ApiPaymentRequest;
import com.green.yp.api.apitype.payment.ApiPaymentResponse;
import com.green.yp.api.apitype.payment.ApplyPaymentRequest;
import com.green.yp.api.apitype.payment.PaymentMethodResponse;
import com.green.yp.common.dto.ResponseApi;
import com.green.yp.config.security.AuthUser;
import com.green.yp.config.security.AuthenticatedUser;
import com.green.yp.exception.SystemException;
import com.green.yp.security.IsAdmin;
import com.green.yp.security.IsAnyAuthenticatedUser;
import com.green.yp.util.RequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
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

  @ApiResponse(
      responseCode = org.apache.hc.core5.http.HttpStatus.SC_NO_CONTENT + "",
      description = "Returns the requested account information")
  @PostMapping(path = "/{accountId}/validate")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void validateEmail(
      @PathVariable(name = "accountId") UUID accountId,
      @RequestParam(name = "contactId", required = false) UUID contactId,
      @RequestParam(name = "email", required = false) String email,
      @RequestParam(name = "validationToken") String validationToken,
      HttpServletRequest request) {
    accountService.validateEmail(
        accountId, contactId, email, validationToken, RequestUtil.getRequestIP(request));
  }

  @IsAnyAuthenticatedUser
  @ApiResponse(
      responseCode = org.apache.hc.core5.http.HttpStatus.SC_OK + "",
      description = "Returns the requested account information",
      content = @Content(mediaType = "application/json"))
  @GetMapping(path = "/user/{externUserRef}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<AccountResponse> findAccountUserRef(
      @PathVariable(name = "externUserRef") String externalUserRef) {
    return new ResponseApi<>(
        accountService.findUserAccount(externalUserRef, RequestUtil.getRequestIP()), null);
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
      @RequestBody @Valid ApiPaymentRequest paymentRequest) {
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

  @IsAnyAuthenticatedUser
  @Operation(summary = "Applies a payment on an existing subscription with an invoice")
  @PostMapping(
      path = "/replace/payment",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseApi<PaymentMethodResponse> replacePayment(
      @Parameter(hidden = true) @AuthUser AuthenticatedUser authenticatedUser,
      @RequestBody @Valid ApiPaymentRequest paymentRequest,
      HttpServletRequest request) {
    return new ResponseApi<>(
        paymentService.replacePayment(
            paymentRequest, authenticatedUser, RequestUtil.getRequestIP(request)),
        null);
  }

  @IsAnyAuthenticatedUser
  @Operation(summary = "Update the subscriber / producer account business profile")
  @PutMapping(path = "/{accountId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseApi<AccountResponse> updateAccount(
         @PathVariable(name="accountId") UUID accountId,
      @Parameter(hidden = true) @AuthUser AuthenticatedUser authenticatedUser,
      @RequestBody @Valid UpdateAccountRequest account) {
    try {
      return new ResponseApi<>(
          accountService.updateAccount(
              Optional.empty(), account, authenticatedUser.userId(), RequestUtil.getRequestIP()),
          null);
    } catch (NoSuchAlgorithmException e) {
      throw new SystemException("System error, missing configuration", e);
    }
  }

  @IsAdmin
  @Operation(
      summary = "Removes / disables unpaid accounts",
      description =
          "Removes or disables unpaid accounts where a payment has not been received in over specified number of days")
  @PostMapping(path = "/clean/unpaid", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseApi<String> cleanUnpaidAccounts(
      @Parameter(hidden = true) @AuthUser AuthenticatedUser authenticatedUser,
      @RequestParam(name = "days", defaultValue = "30", required = false) Integer daysOld) {
    return new ResponseApi<>(
        paymentService.cleanAbandonedAccounts(daysOld, RequestUtil.getRequestIP()), null);
  }

  @Scheduled(cron = "0 0 0,12 * * *")
  public void cleanAbandonedAccounts(){
    paymentService.cleanAbandonedAccounts(5);
  }

  @IsAnyAuthenticatedUser
  @Operation(summary = "Cancels or disables an account")
  @DeleteMapping(path = "/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseApi<String> cancelAccount(
      @Parameter(hidden = true) @AuthUser AuthenticatedUser authenticatedUser,
      @PathVariable UUID accountId) {
    return new ResponseApi<>(
        accountService.cancelAccount(
            accountId, authenticatedUser.userId(), RequestUtil.getRequestIP()),
        null);
  }
}
