package com.green.yp.account.service;

import com.green.yp.account.mapper.AccountMapper;
import com.green.yp.api.apitype.account.AccountResponse;
import com.green.yp.api.apitype.account.CreateAccountRequest;
import com.green.yp.api.apitype.account.UpdateAccountRequest;
import com.green.yp.api.apitype.enumeration.EmailTemplateName;
import com.green.yp.api.apitype.invoice.InvoiceResponse;
import com.green.yp.api.apitype.payment.ApiPaymentResponse;
import com.green.yp.api.apitype.payment.ApplyPaymentMethodRequest;
import com.green.yp.api.apitype.payment.ApplyPaymentRequest;
import com.green.yp.api.apitype.payment.PaymentResponse;
import com.green.yp.api.apitype.producer.*;
import com.green.yp.api.apitype.producer.enumeration.ProducerSubscriptionType;
import com.green.yp.api.contract.*;
import com.green.yp.email.service.EmailService;
import com.green.yp.exception.BusinessException;
import com.green.yp.exception.NotFoundException;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.payment.data.enumeration.ProducerPaymentType;
import jakarta.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
/**
 * Account Orchestration service used to 1. Create the account 2. Cancel the account 3. Apply
 * initial payment to make the account "live"
 */
public class AccountService {

  private final EmailService emailService;

  private final InvoiceContract invoiceContract;

  private final ProducerContract producerContract;

  private final PaymentContract paymentContract;

  private final ProducerContactContract contactContract;

  private final ProducerLocationContract locationContract;

  private final AccountMapper accountMapper;

  public AccountService(
      EmailService emailService,
      InvoiceContract invoiceContract,
      ProducerContract producerContract,
      PaymentContract paymentContract,
      ProducerContactContract contactContract,
      ProducerLocationContract locationContract,
      AccountMapper accountMapper) {
    this.emailService = emailService;
    this.invoiceContract = invoiceContract;
    this.producerContract = producerContract;
    this.paymentContract = paymentContract;
    this.contactContract = contactContract;
    this.locationContract = locationContract;
    this.accountMapper = accountMapper;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public String cancelAccount(UUID accountId, String ipAddress) {
    log.info("Cancelling subscription for {} from ipAddress {}", accountId, ipAddress);

    contactContract.cancelAuthentication(accountId, null, ipAddress);

    producerContract.cancelSubscription(accountId, null, ipAddress);

    paymentContract.cancelBilling(accountId, null, ipAddress);

    List<String> adminEmails = getAdminEmails(accountId);

    emailService.sendEmail(
        EmailTemplateName.ACCOUNT_CANCELLATION,
        producerContract.findProducer(accountId),
        adminEmails.toArray(new String[0]));

    return """
                Your GreenYP listing subscription has been cancelled. The listing will remain active until the end of the month.
                """;
  }

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public AccountResponse createAccount(CreateAccountRequest account, String ipAddress)
      throws NoSuchAlgorithmException {
    log.info("Creating a new account for businessName: {}", account.getBusinessName());

    if (StringUtils.isNotBlank(account.getWebsiteUrl())) {
      List<ProducerResponse> producers = producerContract.findProducer(account.getWebsiteUrl());
      if (CollectionUtils.isNotEmpty(producers)) {
        throw new PreconditionFailedException(
            "The business %s identified by %s appears to already exist",
            account.getBusinessName(), account.getWebsiteUrl());
      }
    }
    // create producer record
    ProducerResponse producerResponse =
        producerContract.createProducer(accountMapper.toProducer(account), ipAddress);
    return new AccountResponse(producerResponse, null, null, null);
  }

  /**
   * Applies the first subscription payment and makes the account go "live".
   *
   * @param paymentRequest
   * @param requestIP
   * @return
   */
  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public ApiPaymentResponse applyInitialPayment(
      @NotNull @NonNull ApplyPaymentMethodRequest paymentRequest,
      @NotNull @NonNull String requestIP) {
    log.info("Apply initial subscription payment for {}", paymentRequest.producerId());

    ProducerResponse producerResponse = producerContract.findProducer(paymentRequest.producerId());
    if (producerResponse.subscriptionType() == ProducerSubscriptionType.LIVE_ACTIVE) {
      throw new PreconditionFailedException("Producer/Account is already active");
    }

    // create invoice record for initial payment, as this is the first payment on a
    // new subscriber, no invoice is created until this point
    InvoiceResponse invoiceResponse =
        invoiceContract.createInvoice(paymentRequest.producerId(), requestIP);

    PaymentResponse paymentResponse =
        paymentContract.applyPayment(
            paymentRequest,
            invoiceResponse.invoiceId(),
            ProducerPaymentType.INITIAL_PAYMENT,
            requestIP);

    if (!paymentResponse.isSuccess()) {
      log.info(
          "Subscription payment was not successful for {} reasonCode {}",
          paymentRequest.producerId(),
          paymentResponse.responseCode());

      return new ApiPaymentResponse(
          paymentResponse.isSuccess(),
          paymentResponse.responseCode(),
          paymentResponse.responseText());
    }

    producerResponse =
        producerContract.activateProducer(
            paymentRequest.producerId(),
            invoiceResponse.createDate(),
            paymentResponse.createDate(),
            null,
            requestIP);

    emailService.sendEmail(
        EmailTemplateName.WELCOME_EMAIL,
        producerResponse,
        getAdminEmails(paymentRequest.producerId()).toArray(new String[0]));

    return new ApiPaymentResponse(
        true, paymentResponse.responseCode(), paymentResponse.responseText());
  }

  public ApiPaymentResponse applyPayment(
      ApplyPaymentRequest paymentRequest, String userId, String requestIP) {
    log.info(
        "Apply subscription payment for invoice {} and producer/account {}",
        paymentRequest.invoiceId(),
        paymentRequest.producerId());

    if (paymentRequest.savedPaymentMethodId() == null
        && paymentRequest.newPaymentMethod() == null) {
      throw new PreconditionFailedException(
          "Existing payment method not provided or new payment method specified");
    }

    invoiceContract.findInvoice(paymentRequest.invoiceId(), requestIP);

    PaymentResponse paymentResponse =
        paymentContract.applyPayment(paymentRequest, userId, requestIP);

    //        emailService.sendEmail(EmailTemplateName.WELCOME_EMAIL, producerResponse,
    //                getAdminEmails(paymentRequest.producerId()).toArray(new String[0]));

    return new ApiPaymentResponse(
        true, paymentResponse.responseCode(), paymentResponse.responseText());
  }

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public AccountResponse updateAccount(UpdateAccountRequest account, String ipAddress)
      throws NoSuchAlgorithmException {

    ProducerResponse producerResponse = null;
    if (account.getProducerRequest() != null) {
      producerResponse = producerContract.updateProducer(account.getProducerRequest());
    } else {
      producerResponse = producerContract.findProducer(account.getProducerId());
    }

    // validate contact, must have contact type of PRIMARY or ADMIN for account creation
    if (account.getPrimaryContact() != null) {
      if (!account.getPrimaryContact().producerContactType().isAccountCreation()) {
        log.info(
            "Attempt to create account with invalid contact, contact must be ADMIN or PRIMARY");
        throw new PreconditionFailedException("ContactType must be one of ADMIN or PRIMARY");
      }
    }

    ProducerLocationResponse locationResponse = null;
    // create or update primary location
    try {
      locationResponse = locationContract.findPrimaryLocation(account.getProducerId());
    } catch (NotFoundException pfe) {
      log.info("No Primary location found for {}", account.getProducerId());
    }
    if (account.getPrimaryLocation() != null) {
      LocationRequest request = account.getPrimaryLocation();
      if (locationResponse != null) {
        if (request.locationId() != null
            && !request.locationId().equals(locationResponse.locationId())) {
          throw new BusinessException(
              "Attempting to update primary location with another location");
        }
        request = accountMapper.copyRequest(request, locationResponse.locationId());
      }
      locationResponse =
          locationContract.updatePrimaryLocation(account.getProducerId(), request, ipAddress);
    }

    // create initial contact record
    List<ProducerContactResponse> adminContacts =
        contactContract.findAdminContacts(account.getProducerId());
    if (account.getPrimaryContact() != null) {
      ProducerContactRequest request = account.getPrimaryContact();
      ProducerContactResponse adminContact = getAdminContact(adminContacts, request);

      if (locationResponse == null) {
        log.info("No Primary location found for {}", account.getProducerId());
        throw new PreconditionFailedException(
            "Primary location is required before creating primary contact");
      }

      if (adminContact != null) {
        request =
            accountMapper.copyRequest(
                request, adminContact.contactId(), locationResponse.locationId());
      }

      contactContract.updatePrimaryContact(
          request, account.getProducerId(), locationResponse.locationId(), ipAddress);
      adminContacts = contactContract.findAdminContacts(account.getProducerId());
    }

    // create admin/master user credentials
    ProducerCredentialsResponse credentialsResponse = null;
    try {
      credentialsResponse = producerContract.findMasterUserCredentials(account.getProducerId());
    } catch (NotFoundException nfe) {
      log.info("Master user admin credentials not created for {}", account.getProducerId());
    }
    if (account.getMasterUserCredentials() != null) {
      if (account.getMasterUserCredentials().producerContactId() == null) {
        log.error("Master User credentials must have a producer contact, no contact was provided");
        throw new PreconditionFailedException(
            "Master User credentials must have a producer contact, no contact was provided");
      }
      adminContacts.stream()
          .filter(c -> c.contactId().equals(account.getMasterUserCredentials().producerContactId()))
          .findFirst()
          .orElseThrow(
              () -> {
                log.error("Specified contact id is not a primary account contact");
                return new PreconditionFailedException(
                    "Specified contact id is not a primary account contact");
              });
      if (credentialsResponse == null) {
        credentialsResponse =
            producerContract.createMasterUserCredentials(
                account.getMasterUserCredentials(),
                account.getMasterUserCredentials().emailAddress(),
                account.getProducerId(),
                account.getMasterUserCredentials().producerContactId(),
                ipAddress);
      } else if (isModifyingExistingCredentials(
          account.getMasterUserCredentials(), credentialsResponse)) {
        credentialsResponse =
            producerContract.updateMasterCredentials(
                account.getMasterUserCredentials(),
                account.getProducerId(),
                credentialsResponse.credentialsId(),
                ipAddress);
      } else { // replacing master user credentials
        credentialsResponse =
            producerContract.replaceMasterUserCredentials(
                account.getProducerId(),
                credentialsResponse.credentialsId(),
                account.getMasterUserCredentials(),
                ipAddress);
      }
    }
    return new AccountResponse(
        producerResponse, locationResponse, adminContacts, credentialsResponse);
  }

  private boolean isModifyingExistingCredentials(
      UserCredentialsRequest masterUserCredentials,
      ProducerCredentialsResponse credentialsResponse) {
    return credentialsResponse.firstName().equals(masterUserCredentials.firstName())
        && credentialsResponse.lastName().equals(masterUserCredentials.lastName())
        && (credentialsResponse.emailAddress().equals(masterUserCredentials.emailAddress())
            || credentialsResponse.userName().equals(masterUserCredentials.userName()));
  }

  private ProducerContactResponse getAdminContact(
      List<ProducerContactResponse> adminContacts, ProducerContactRequest request) {
    try {
      return adminContacts.stream()
          .filter(
              contact ->
                  contact.contactId().equals(request.contactId())
                      || contact.emailAddress().equals(request.emailAddress()))
          .findFirst()
          .get();
    } catch (Exception e) {
      log.info(
          "No Primary contact exists for contact {} or email {} to update",
          request.contactId(),
          request.emailAddress());
      return null;
    }
  }

  public AccountResponse findAccount(@NotNull @NonNull UUID accountId) {

    ProducerResponse producerResponse = producerContract.findProducer(accountId);

    ProducerLocationResponse locationResponse = producerContract.findPrimaryLocation(accountId);

    List<ProducerContactResponse> contacts =
        contactContract.findContacts(accountId, locationResponse.locationId());

    log.info("Returning producer account information for {}", accountId);

    return new AccountResponse(producerResponse, locationResponse, contacts, null);
  }

  private List<String> getAdminEmails(UUID accountId) {
    List<ProducerContactResponse> contacts = contactContract.findAdminContacts(accountId);

    return contacts.stream().map(contact -> contact.emailAddress()).toList();
  }

  /**
   * The purpose of this method is to clean up records corresponding to any subscriptions started
   * but never finished or made active.
   *
   * @param daysOld
   * @return
   */
  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public String cleanUnpaidAccounts(@NotNull @NonNull Integer daysOld, String ipAddress) {
    log.info("Removing unpaid account records and credentials");

    List<ProducerResponse> producers =
        producerContract.findLastModified(daysOld, ProducerSubscriptionType.LIVE_UNPAID);
    if (CollectionUtils.isEmpty(producers)) {
      return String.format("No unpaid subscribers over %s days old", daysOld);
    }
    List<UUID> producerIds = producers.stream().map(p -> p.producerId()).toList();

    producerContract.deleteCredentials(producerIds);

    contactContract.deleteContacts(producerIds);

    locationContract.deleteLocation(producerIds);

    producerContract.deleteProducers(producerIds, ipAddress);

    log.info("Removed {} unpaid account subscriptions", producers.size());

    return String.format(
        "Removed %s unpaid account subscriptions over %s days old", producerIds.size(), daysOld);
  }
}
