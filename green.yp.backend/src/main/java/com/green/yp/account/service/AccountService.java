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

  private final ProducerContract producerContract;

  private final PaymentContract paymentContract;

  private final ProducerContactContract contactContract;

  private final ProducerLocationContract locationContract;

  private final AccountMapper accountMapper;

  public AccountService(
      EmailService emailService,
      ProducerContract producerContract,
      PaymentContract paymentContract,
      ProducerContactContract contactContract,
      ProducerLocationContract locationContract,
      AccountMapper accountMapper) {
    this.emailService = emailService;
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
    log.info(
        "Creating a new account for businessName: {}", account.producerRequest().businessName());

    if (StringUtils.isNotBlank(account.producerRequest().websiteUrl())) {
      List<ProducerResponse> producers =
          producerContract.findProducer(account.producerRequest().websiteUrl());
      if (CollectionUtils.isNotEmpty(producers)) {
        throw new PreconditionFailedException(
            "The business %s identified by %s appears to already exist",
            account.producerRequest().businessName(), account.producerRequest().websiteUrl());
      }
    }
    // create producer record
    ProducerResponse producerResponse =
        producerContract.createProducer(accountMapper.toProducer(account), ipAddress);
    return new AccountResponse(producerResponse, null, null, null);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public AccountResponse updateAccount(UpdateAccountRequest account, String ipAddress)
      throws NoSuchAlgorithmException {

    ProducerResponse producerResponse = null;
    if (account.producerRequest() != null) {
      producerResponse = producerContract.updateProducer(account.producerRequest());
    } else {
      producerResponse = producerContract.findProducer(account.producerId());
    }

    // validate contact, must have contact type of PRIMARY or ADMIN for account creation
    if (account.primaryContact() != null) {
      if (!account.primaryContact().producerContactType().isAccountCreation()) {
        log.info(
            "Attempt to create account with invalid contact, contact must be ADMIN or PRIMARY");
        throw new PreconditionFailedException("ContactType must be one of ADMIN or PRIMARY");
      }
    }

    ProducerLocationResponse locationResponse = null;
    // create or update primary location
    try {
      locationResponse = locationContract.findPrimaryLocation(account.producerId());
    } catch (NotFoundException pfe) {
      log.info("No Primary location found for {}", account.producerId());
    }
    if (account.primaryLocation() != null) {
      LocationRequest request = account.primaryLocation();
      if (locationResponse != null) {
        if (request.locationId() != null
            && !request.locationId().equals(locationResponse.locationId())) {
          throw new BusinessException(
              "Attempting to update primary location with another location");
        }
        request = accountMapper.copyRequest(request, locationResponse.locationId());
      }
      locationResponse =
          locationContract.updatePrimaryLocation(account.producerId(), request, ipAddress);
    }

    // create initial contact record
    List<ProducerContactResponse> adminContacts =
        contactContract.findAdminContacts(account.producerId());
    if (account.primaryContact() != null) {
      ProducerContactRequest request = account.primaryContact();
      ProducerContactResponse adminContact = getAdminContact(adminContacts, request);

      if (locationResponse == null) {
        log.info("No Primary location found for {}", account.producerId());
        throw new PreconditionFailedException(
            "Primary location is required before creating primary contact");
      }

      if (adminContact != null) {
        request =
            accountMapper.copyRequest(
                request, adminContact.contactId(), locationResponse.locationId());
      }

      contactContract.updatePrimaryContact(
          request, account.producerId(), locationResponse.locationId(), ipAddress);
      adminContacts =
          contactContract.findAdminContacts(account.producerRequest().lineOfBusinessId());
    }

    // create admin/master user credentials
    ProducerCredentialsResponse credentialsResponse = null;
    try {
      credentialsResponse = producerContract.findMasterUserCredentials(account.producerId());
    } catch (NotFoundException nfe) {
      log.info("Master user admin credentials not created for {}", account.producerId());
    }
    if (account.masterUserCredentials() != null) {
      if (account.masterUserCredentials().producerContactId() == null) {
        log.error("Master User credentials must have a producer contact, no contact was provided");
        throw new PreconditionFailedException(
            "Master User credentials must have a producer contact, no contact was provided");
      }
      adminContacts.stream()
          .filter(c -> c.contactId().equals(account.masterUserCredentials().producerContactId()))
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
                account.masterUserCredentials(),
                account.masterUserCredentials().emailAddress(),
                account.producerId(),
                account.masterUserCredentials().producerContactId(),
                ipAddress);
      } else if (isModifyingExistingCredentials(
          account.masterUserCredentials(), credentialsResponse)) {
        credentialsResponse =
            producerContract.updateMasterCredentials(
                account.masterUserCredentials(),
                account.producerId(),
                credentialsResponse.credentialsId(),
                ipAddress);
      } else { // replacing master user credentials
        credentialsResponse =
            producerContract.replaceMasterUserCredentials(
                account.producerId(),
                credentialsResponse.credentialsId(),
                account.masterUserCredentials(),
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
}
