package com.green.yp.producer.service;

import com.green.yp.api.apitype.producer.ProducerCredentialsResponse;
import com.green.yp.api.apitype.producer.UserCredentialsRequest;
import com.green.yp.api.apitype.producer.enumeration.ProducerContactType;
import com.green.yp.api.contract.AuditContract;
import com.green.yp.api.contract.AuthenticationContract;
import com.green.yp.auth.model.AuthServiceResponse;
import com.green.yp.config.CredentialsConfig;
import com.green.yp.exception.NotFoundException;
import com.green.yp.exception.PreconditionFailedException;
import com.green.yp.exception.producer.ContactAccountLockedException;
import com.green.yp.producer.data.model.*;
import com.green.yp.producer.data.repository.InvalidCredentialsCounterRepository;
import com.green.yp.producer.data.repository.ProducerUserCredentialsRepository;
import com.green.yp.producer.mapper.ProducerAuthUserMapper;
import io.fusionauth.domain.api.UserResponse;
import io.fusionauth.domain.api.user.RegistrationResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;

@Slf4j
@Service
public class ProducerUserService {

  public static final String CREATE_MASTER_ADMIN_USER_LOG_MESSAGE =
      "Create master admin user as {} for {}";
  final ProducerOrchestrationService producerService;

  final ProducerAuthUserMapper authUserMapper;

  final ProducerContactService contactService;

  final ProducerUserCredentialsRepository credentialsRepository;

  final InvalidCredentialsCounterRepository credentialsCounterRepository;

  private final CredentialsConfig credentialsConfig;

  private final AuditContract auditContract;

  private final AuthenticationContract authenticationContract;

  public ProducerUserService(
      ProducerOrchestrationService producerService,
      ProducerAuthUserMapper authUserMapper,
      ProducerContactService contactService,
      ProducerUserCredentialsRepository credentialsRepository,
      InvalidCredentialsCounterRepository credentialsCounterRepository,
      CredentialsConfig credentialsConfig,
      AuditContract auditContract,
      AuthenticationContract authenticationContract) {
    this.producerService = producerService;
    this.authUserMapper = authUserMapper;
    this.contactService = contactService;
    this.credentialsRepository = credentialsRepository;
    this.credentialsCounterRepository = credentialsCounterRepository;
    this.credentialsConfig = credentialsConfig;
    this.auditContract = auditContract;
    this.authenticationContract = authenticationContract;
  }

  public ProducerCredentialsResponse createMasterUserCredentials(
      UserCredentialsRequest credentialsRequest,
      String emailAddress,
      UUID producerId,
      UUID contactId,
      String ipAddress)
      throws NoSuchAlgorithmException {
    log.info(CREATE_MASTER_ADMIN_USER_LOG_MESSAGE, credentialsRequest.userName(), producerId);

    return createCredentials(
        credentialsRequest, true, emailAddress, producerId, contactId, ipAddress);
  }

  public ProducerCredentialsResponse createUserCredentials(
      UserCredentialsRequest credentialsRequest,
      String emailAddress,
      UUID producerId,
      UUID contactId,
      String ipAddress)
      throws NoSuchAlgorithmException {
    log.info(CREATE_MASTER_ADMIN_USER_LOG_MESSAGE, credentialsRequest.userName(), producerId);
    return createCredentials(
        credentialsRequest, false, emailAddress, producerId, contactId, ipAddress);
  }

  ProducerCredentialsResponse createCredentials(
      @NonNull @NotNull UserCredentialsRequest credentialsRequest,
      Boolean accountMaster,
      String emailAddress,
      @NonNull @NotNull UUID producerId,
      @NonNull @NotNull UUID contactId,
      String ipAddress)
      throws NoSuchAlgorithmException {
    log.info(CREATE_MASTER_ADMIN_USER_LOG_MESSAGE, credentialsRequest.userName(), producerId);

    producerService.findActiveProducer(producerId);

    if (contactId != null) {
      contactService.findActiveContact(contactId);
    }

    AuthServiceResponse<RegistrationResponse> response =
        authenticationContract.registerUser(
            producerId, contactId, accountMaster, credentialsRequest);

    ProducerUserCredentials authorizedUser = authUserMapper.toEntity(credentialsRequest);
    authorizedUser.setPassword(createPasswordHash(credentialsRequest.credentials()));
    authorizedUser.setProducerId(producerId);
    authorizedUser.setProducerContactId(contactId);
    authorizedUser.setLastChangeDate(OffsetDateTime.now());
    authorizedUser.setEnabled(true);
    if (StringUtils.isBlank(authorizedUser.getEmailAddress())) {
      authorizedUser.setEmailAddress(emailAddress);
    }
    authorizedUser.setExternalAuthorizationServiceRef(
        response.getActualResponse().user.id.toString());
    authorizedUser.setRegistrationRef(response.getActualResponse().registration.id.toString());
    authorizedUser.setAdminUser(accountMaster);

    ProducerUserCredentials savedCredentials = credentialsRepository.saveAndFlush(authorizedUser);

    return authUserMapper.fromEntity(savedCredentials);
  }

  public ProducerCredentialsResponse authorizeUser(
      @NonNull String userId, @NonNull String password, @NonNull String ipAddress)
      throws NoSuchAlgorithmException, InvalidCredentialsException {

    Integer badAttempts =
        credentialsCounterRepository.countBadAttempts(userId, password, ipAddress);
    if (credentialsConfig.getMaxAttemptsCounter().compareTo(badAttempts) == 0) {
      auditContract.createBadAuthenticationAttempt(userId, password, ipAddress, badAttempts);
      throw new ContactAccountLockedException(credentialsConfig.getTimeLockMinutes());
    }

    ProducerUserCredentials savedCredentials =
        credentialsRepository
            .findCredentials(userId, createPasswordHash(password))
            .orElseThrow(
                () -> {
                  incrementAttemptCounter(userId, password, ipAddress);
                  return new InvalidCredentialsException("Invalid userId or password");
                });

    return authUserMapper.fromEntity(savedCredentials);
  }

  public void disableAuthentication(@NonNull @NotNull UUID contactId) {
    log.info("Disable any credentials /users associated with contact {}", contactId);
    credentialsRepository.findContactCredentials(contactId).stream()
        .forEach(
            creds -> {
              creds.setEnabled(false);
              credentialsRepository.save(creds);
              authenticationContract.disableUser(creds.getExternalAuthorizationServiceRef());
            });
  }

  private void incrementAttemptCounter(String userId, String password, String ipAddress) {
    Optional<ProducerUserCredentials> credentialsOptional =
        credentialsRepository.findCredentials(userId);

    InvalidCredentialsCounter counter =
        credentialsOptional
            .map(
                creds ->
                    InvalidCredentialsCounter.builder()
                        .userId(userId)
                        .badCreds(password)
                        .ipAddress(ipAddress)
                        .userCredentialsId(creds.getId())
                        .build())
            .orElse(
                InvalidCredentialsCounter.builder()
                    .userId(userId)
                    .badCreds(password)
                    .ipAddress(ipAddress)
                    .build());

    credentialsCounterRepository.saveAndFlush(counter);
  }

  public List<ProducerCredentialsResponse> findUsers(
      @NotNull @NonNull UUID producerId, String firstName, String lastName, String ipAddress) {
    log.info("Return credentialed users for {} filtered by {} {}", producerId, firstName, lastName);
    firstName = StringUtils.isBlank(firstName) ? null : firstName;
    lastName = StringUtils.isBlank(lastName) ? null : lastName;
    return credentialsRepository.findUsers(producerId, firstName, lastName).stream()
        .map(authUserMapper::fromEntity)
        .toList();
  }

  public Optional<ProducerCredentialsResponse> findCredentials(
      String userName, String emailAddress) {
    log.debug(
        "Find existing credentials by userName {} or emailAddress {}", userName, emailAddress);

    return credentialsRepository
        .findCredentialsByUserName(userName, emailAddress)
        .map(authUserMapper::fromEntity)
        .or(
            () -> {
              authenticationContract
                  .findUser(userName, emailAddress)
                  .ifPresent(
                      creds ->
                          authenticationContract.removeUser(
                              creds.externalAuthorizationServiceRef()));
              return Optional.empty();
            });
  }

  public Optional<ProducerCredentialsResponse> findCredentialByRef(
      String externalUserRef, String ipAddress) {
    log.debug("Attempting to find credentials for ref {}", externalUserRef);
    return credentialsRepository
        .findByExternalAuthorizationServiceRef(externalUserRef)
        .map(authUserMapper::fromEntity);
  }

  public ProducerCredentialsResponse findMasterUserCredentials(UUID producerId) {
    log.info("Loading master user credentials for producer {}", producerId);
    return credentialsRepository
        .findMasterUserCredentials(producerId)
        .map(authUserMapper::fromEntity)
        .orElseThrow(() -> new NotFoundException("Master user admin credentials not found"));
  }

  @Transactional
  public ProducerCredentialsResponse updateUserCredentials(
      UserCredentialsRequest userCredentialsRequest,
      UUID producerId,
      UUID credentialsId,
      String ipAddress)
      throws NoSuchAlgorithmException {
    log.info(
        "Updating existing user credentials for producer {} and contact {}",
        producerId,
        userCredentialsRequest.producerContactId());

    ProducerUserCredentials credentials =
        credentialsRepository
            .findById(credentialsId)
            .orElseThrow(
                () -> {
                  log.error("No credentials record found for {}", credentialsId);
                  return new NotFoundException("credentials", credentialsId);
                });

    if (!credentials.getEnabled()) {
      log.error("Cannot update / modify disabled credentials for credentailsId {}", credentialsId);
      throw new PreconditionFailedException("Cannot update / modify disabled credentials");
    }

    AuthServiceResponse<UserResponse> response =
        authenticationContract.findUser(credentials.getExternalAuthorizationServiceRef());
    if (!response.getActualResponse().user.active) {
      log.error(
          "Cannot modify disabled fusionAuth credentials for credentailsId {}", credentialsId);
      throw new PreconditionFailedException("Cannot update / modify disabled credentials");
    }

    AuthServiceResponse<UserResponse> updatedUser =
        authenticationContract.modifyUser(
            producerId, credentials.getExternalAuthorizationServiceRef(), userCredentialsRequest);

    credentials.setUserId(updatedUser.getActualResponse().user.username);
    credentials.setEmailAddress(updatedUser.getActualResponse().user.email);
    credentials.setFirstName(updatedUser.getActualResponse().user.firstName);
    credentials.setLastName(updatedUser.getActualResponse().user.lastName);
    credentials.setLastChangeDate(OffsetDateTime.now());
    credentials.setPassword(createPasswordHash(userCredentialsRequest.credentials()));

    return authUserMapper.fromEntity(credentialsRepository.saveAndFlush(credentials));
  }

  @Transactional
  public ProducerCredentialsResponse replaceMasterUserCredentials(
      @NotNull @NonNull UserCredentialsRequest masterUserCredentials,
      @NotNull @NonNull UUID producerId,
      @NotNull @NonNull String ipAddress)
      throws NoSuchAlgorithmException {
    log.info("Replacing subscriber master admin user credentials for {}", producerId);

    UUID contactId;
    if (masterUserCredentials.producerContactId() == null) {
      contactId =
          contactService.findActiveContact(masterUserCredentials.producerContactId()).getId();
    } else {
      contactId =
          contactService.findAdminContacts(producerId).stream()
              .filter(
                  contact ->
                      contact.producerContactType() == ProducerContactType.PRIMARY
                          && contact.emailAddress().equals(masterUserCredentials.emailAddress()))
              .findFirst()
              .orElseThrow(
                  () -> {
                    log.error(
                        "No {} contact for subscriber {}", ProducerContactType.PRIMARY, producerId);
                    return new NotFoundException("Cannot find PRIMARY contact for subscriber");
                  })
              .contactId();
    }

    ProducerUserCredentials credentials =
        credentialsRepository
            .findMasterUserCredentials(producerId)
            .orElseThrow(
                () -> {
                  log.error("Cannot find master user credentials for subscriber {}", producerId);
                  return new NotFoundException("Cannot find master user credentials for account");
                });

    authenticationContract.disableUser(credentials.getExternalAuthorizationServiceRef());

    credentials.setEnabled(false);
    credentialsRepository.saveAndFlush(credentials);

    return createCredentials(
        masterUserCredentials,
        true,
        masterUserCredentials.emailAddress(),
        producerId,
        contactId,
        ipAddress);
  }

  @org.springframework.transaction.annotation.Transactional(
      isolation = Isolation.READ_COMMITTED,
      propagation = Propagation.REQUIRED)
  //    @AuditRequest(requestParameter = "producerIds",
  //            objectType = AuditObjectType.PRODUCER_LOCATION, actionType =
  //            AuditActionType.DELETE_LOCATION)
  public void removeCredentials(List<UUID> producerIdList) {
    log.info("Removing credentials for producers {}", producerIdList);
    List<ProducerUserCredentials> credentials =
        credentialsRepository.findCredentials(producerIdList);
    credentials.stream()
        .filter(creds -> StringUtils.isNotBlank(creds.getExternalAuthorizationServiceRef()))
        .forEach(
            creds -> authenticationContract.removeUser(creds.getExternalAuthorizationServiceRef()));

    credentialsRepository.deleteByProducerIds(producerIdList);

    log.info("Completed removing/deleting credentials for producers {}", producerIdList);
  }

  private String createPasswordHash(String password) throws NoSuchAlgorithmException {
    final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
    final byte[] hashbytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
    return bytesToHex(hashbytes);
  }

  private static String bytesToHex(byte[] hash) {
    StringBuilder hexString = new StringBuilder(2 * hash.length);
    for (int i = 0; i < hash.length; i++) {
      String hex = Integer.toHexString(0xff & hash[i]);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }
}
