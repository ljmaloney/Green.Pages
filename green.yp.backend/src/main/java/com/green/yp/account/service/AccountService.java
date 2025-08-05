package com.green.yp.account.service;

import com.green.yp.account.mapper.AccountMapper;
import com.green.yp.api.AuditRequest;
import com.green.yp.api.apitype.account.AccountResponse;
import com.green.yp.api.apitype.account.CreateAccountRequest;
import com.green.yp.api.apitype.account.UpdateAccountRequest;
import com.green.yp.api.apitype.enumeration.AuditActionType;
import com.green.yp.api.apitype.enumeration.AuditObjectType;
import com.green.yp.api.apitype.enumeration.EmailTemplateType;
import com.green.yp.api.apitype.producer.*;
import com.green.yp.api.apitype.producer.enumeration.ProducerContactType;
import com.green.yp.api.apitype.producer.enumeration.ProducerDisplayContactType;
import com.green.yp.api.contract.*;
import com.green.yp.email.service.EmailService;
import com.green.yp.exception.*;
import jakarta.validation.constraints.NotNull;
import java.lang.Thread;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
/**
 * Account Orchestration service used to
 * 1. Create the account
 * 2. Cancel the account
 * 3. Apply initial payment to make the account "live"
 */
public class AccountService {

  private final EmailService emailService;

  private final ProducerContract producerContract;

  private final PaymentContract paymentContract;

  private final ProducerContactContract contactContract;

  private final ProducerLocationContract locationContract;
  private final AccountPaymentService paymentService;
  private final AccountMapper accountMapper;
  private final ProducerContactContract producerContactContract;

  @Value("${green.yp.pro.subscription.renewal.threads:5}")
  private int renewalThreads;

  public AccountService(
          EmailService emailService,
          ProducerContract producerContract,
          PaymentContract paymentContract,
          ProducerContactContract contactContract,
          ProducerLocationContract locationContract, AccountPaymentService paymentService,
          AccountMapper accountMapper,
          ProducerContactContract producerContactContract) {
    this.emailService = emailService;
    this.producerContract = producerContract;
    this.paymentContract = paymentContract;
    this.contactContract = contactContract;
    this.locationContract = locationContract;
      this.paymentService = paymentService;
      this.accountMapper = accountMapper;
    this.producerContactContract = producerContactContract;
  }

  @AuditRequest(
      requestParameter = "accountId",
      actionType = AuditActionType.CANCEL_ACCOUNT,
      objectType = AuditObjectType.OBJECT)
  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public String cancelAccount(UUID accountId, String userId, String ipAddress) {
    log.info("Cancelling subscription for {} from ipAddress {}", accountId, ipAddress);

    contactContract.cancelAuthentication(accountId, null, ipAddress);

    producerContract.cancelSubscription(accountId, null, ipAddress);

    paymentContract.cancelCardOnFile(accountId.toString());

    List<String> adminEmails = getAdminEmails(accountId);

    emailService.sendEmail(
        EmailTemplateType.ACCOUNT_CANCELLATION,
        producerContract.findProducer(accountId),
        adminEmails.toArray(new String[0]));

    return """
                Your GreenYP listing subscription has been cancelled. The listing will remain active until the end of the month.
                """;
  }

  public AccountResponse findUserAccount(String externalUserRef, String ipAddress) {
    log.info("Loading account info for logged user by ref {}", externalUserRef);

    var credsOptional = producerContract.findCredentialByRef(externalUserRef, ipAddress);
    var producerId =
        credsOptional
            .orElseThrow(
                () -> {
                  log.warn(
                      "No saved record mapping externalUserRef {} to producer/subscriber",
                      externalUserRef);
                  return new NotFoundException(
                      String.format(
                          "No subscription found for user identified by %s", externalUserRef));
                })
            .producerId();

    return findAccount(producerId);
  }

  public AccountResponse findAccount(@NotNull @NonNull UUID accountId) {

    ProducerResponse producerResponse = producerContract.findProducer(accountId);

    ProducerLocationResponse locationResponse = producerContract.findPrimaryLocation(accountId);

    List<ProducerContactResponse> contacts =
        contactContract.findContacts(accountId, locationResponse.locationId());

    log.info("Returning producer account information for {}", accountId);

    return new AccountResponse(producerResponse, locationResponse, contacts, null);
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

    producerContract
        .findCredential(
            account.masterUserCredentials().userName(),
            account.masterUserCredentials().emailAddress())
        .ifPresentOrElse(
            credentials -> {
              log.warn(
                  "Existing user credentials found for username {} or email {}",
                  account.masterUserCredentials().userName(),
                  account.masterUserCredentials().emailAddress());
              throw new UserCredentialsException(
                  account.masterUserCredentials().userName(),
                  account.masterUserCredentials().emailAddress());
            },
            () ->
                log.debug(
                    "No existing user credentials found for username {} or email {}",
                    account.masterUserCredentials().userName(),
                    account.masterUserCredentials().emailAddress()));

    // create producer record
    ProducerResponse producerResponse =
        producerContract.createProducer(account.producerRequest(), ipAddress);

    // validate contact, must have contact type of PRIMARY or ADMIN for account creation
    isValidPrimaryContact(account);

    ProducerLocationResponse locationResponse =
        createOrUpdateLocation(account, producerResponse.producerId(), ipAddress);

    List<ProducerContactResponse> adminContacts =
        contactContract.findAdminContacts(producerResponse.producerId());
    ProducerContactResponse contactResponse =
        createOrUpdateContact(
            producerResponse,
            Optional.of(account.primaryContact()),
            adminContacts,
            locationResponse,
            ipAddress);
    adminContacts.add(contactResponse);

    // create admin/master user credentials
    var credentialsResponse =
        createOrUpdateCredentials(
            producerResponse, account.masterUserCredentials(), contactResponse, ipAddress);

    return new AccountResponse(
        producerResponse, locationResponse, adminContacts, credentialsResponse);
  }

  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public AccountResponse updateAccount(
      Optional<ProducerResponse> producerOptional,
      UpdateAccountRequest account,
      String userId,
      String requestIP)
      throws NoSuchAlgorithmException {

    var producerResponse =
        producerOptional.orElseGet(
            () -> {
              if (account.producerRequest() != null) {
                return producerContract.updateProducer(account.producerRequest());
              }
              return producerContract.findProducer(account.producerId());
            });

    return new AccountResponse(producerResponse, null, null, null);
  }

  public void validateEmail(
      @NotNull @NonNull UUID accountId,
      UUID contactId,
      String email,
      @NotNull @NonNull String validationToken,
      String requestIP) {
    if (contactId != null) {
      contactContract.validateContact(accountId, contactId, validationToken);
    } else if (StringUtils.isNotBlank(email)) {
      contactContract.validateEmail(accountId, email, validationToken);
    } else {
      throw new PreconditionFailedException(
          "Provide either an email address or contactId for the email being validated");
    }
  }

  @Scheduled(fixedDelayString="${green.yp.pro.subscription.renewal.fixedDelay:180}",
          timeUnit = TimeUnit.MINUTES)
  public void processMonthlyPayment(){
    log.info("Begin processing monthly producer / pro subscriptions");

    producerContract.initializePaymentProcessQueue();
      try (var threadPool = new ForkJoinPool(renewalThreads)) {
        List<ProducerResponse> producersToProcess = producerContract.getProducersToProcess(renewalThreads);
        while(CollectionUtils.isNotEmpty(producersToProcess)){
          processPayment(producersToProcess, threadPool);
          producersToProcess = producerContract.getProducersToProcess(renewalThreads);
        }
      }catch (Exception e) {
        log.error("Unexpected error while processing pro subscription renewal payments", e);
      }
    log.info("Completed processing monthly producer / pro subscriptions");
  }

  private void processPayment(List<ProducerResponse> producersToProcess, ForkJoinPool threadPool) {
    var futureComplete = threadPool.submit( () -> producersToProcess.parallelStream().forEach(paymentService::processSubscriptionPayment));
    while (!futureComplete.isDone()) {
      try{
        Thread.sleep(500);
      }catch (InterruptedException ie){
        log.warn("Interrupted while waiting for subscription payment");
      }
    }
  }

  private ProducerCredentialsResponse createOrUpdateCredentials(
      ProducerResponse producerResponse,
      UserCredentialsRequest request,
      ProducerContactResponse contactResponse,
      String ipAddress)
      throws NoSuchAlgorithmException {

    ProducerCredentialsResponse credentialsResponse = null;
    try {
      credentialsResponse =
          producerContract.findMasterUserCredentials(producerResponse.producerId());
    } catch (NotFoundException nfe) {
      log.info("Master user admin credentials not created for {}", producerResponse.producerId());
    }
    if (request != null) {
      UUID credentialContactId = contactResponse.contactId();
      if (credentialsResponse == null) {
        if (createCredentialsContact(request, contactResponse)) {
          var credContact =
              producerContactContract.createContact(
                  new ProducerContactRequest(
                      null,
                      contactResponse.producerLocationId(),
                      ProducerContactType.ADMIN,
                      ProducerDisplayContactType.NO_DISPLAY,
                      null,
                      request.firstName(),
                      request.lastName(),
                      null,
                      request.businessPhone(),
                      request.cellPhone(),
                      request.emailAddress()),
                  producerResponse.producerId(),
                  contactResponse.producerLocationId(),
                  ipAddress);
          log.info(
              "Created new contact for credentials as ADMIN / NO DISPLAY, contactId {}",
              credContact.contactId());
          credentialContactId = credContact.contactId();
        }
        credentialsResponse =
            producerContract.createMasterUserCredentials(
                request,
                request.emailAddress(),
                producerResponse.producerId(),
                credentialContactId,
                ipAddress);
      } else if (isModifyingExistingCredentials(request, credentialsResponse)) {
        credentialsResponse =
            producerContract.updateMasterCredentials(
                request,
                producerResponse.producerId(),
                credentialsResponse.credentialsId(),
                ipAddress);
      } else { // replacing master user credentials
        credentialsResponse =
            producerContract.replaceMasterUserCredentials(
                producerResponse.producerId(),
                credentialsResponse.credentialsId(),
                request,
                ipAddress);
      }
    }
    return credentialsResponse;
  }

  private boolean createCredentialsContact(
      UserCredentialsRequest request, ProducerContactResponse contactResponse) {
    return request.firstName().equals(contactResponse.firstName())
        && request.lastName().equals(contactResponse.lastName())
        && request.emailAddress().equals(contactResponse.emailAddress());
  }

  private ProducerContactResponse createOrUpdateContact(
      ProducerResponse producerResponse,
      Optional<ProducerContactRequest> contactRequest,
      List<ProducerContactResponse> adminContacts,
      ProducerLocationResponse locationResponse,
      String ipAddress) {

    return contactRequest
        .map(
            request -> {
              ProducerContactResponse adminContact = getAdminContact(adminContacts, request);
              if (locationResponse == null) {
                log.info("No Primary location found for {}", producerResponse.producerId());
                throw new PreconditionFailedException(
                    "Primary location is required before creating primary contact");
              }
              if (adminContact != null) {
                request =
                    accountMapper.copyRequest(
                        request, adminContact.contactId(), locationResponse.locationId());
              }

              return contactContract.updatePrimaryContact(
                  request, producerResponse.producerId(), locationResponse.locationId(), ipAddress);
            })
        .orElseThrow(() -> new SystemException("Unexpected error creating account contact", null));
  }

  private ProducerLocationResponse createOrUpdateLocation(
      CreateAccountRequest account, @NonNull @NotNull UUID producerId, String ipAddress) {
    // create or update primary location
    ProducerLocationResponse locationResponse = null;
    try {
      locationResponse = locationContract.findPrimaryLocation(producerId);
    } catch (NotFoundException pfe) {
      log.info("No Primary location found for {}", producerId);
    }
      LocationRequest request = account.primaryLocation();
      if (locationResponse != null) {
        if (request.locationId() != null
            && !request.locationId().equals(locationResponse.locationId())) {
          throw new BusinessException(
              "Attempting to update primary location with another location");
        }
        request = accountMapper.copyRequest(request, locationResponse.locationId());
      }
      locationResponse = locationContract.updatePrimaryLocation(producerId, request, ipAddress);
      return locationResponse;
  }

  private void isValidPrimaryContact(CreateAccountRequest account) {
      if (!account.primaryContact().producerContactType().isAccountCreation()) {
        log.info(
            "Attempt to create account with invalid contact, contact must be ADMIN or PRIMARY");
        throw new PreconditionFailedException("ContactType must be one of ADMIN or PRIMARY");
      }
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
      List<ProducerContactResponse> contacts, ProducerContactRequest request) {
    return contacts.stream()
        .filter(
            contact ->
                contact.contactId().equals(request.contactId())
                    || contact.emailAddress().equals(request.emailAddress()))
        .findFirst()
        .orElseGet(
            () -> {
              log.info(
                  "No Primary contact exists for contact {} or email {} to update",
                  request.contactId(),
                  request.emailAddress());
              return null;
            });
  }

  private List<String> getAdminEmails(UUID accountId) {
    List<ProducerContactResponse> contacts = contactContract.findAdminContacts(accountId);

    return contacts.stream().map(ProducerContactResponse::emailAddress).toList();
  }
}
