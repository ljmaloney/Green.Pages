package com.green.yp.auth.service;

import com.green.yp.api.apitype.account.AccountRoleType;
import com.green.yp.api.apitype.producer.AuthenticatedUserCredentialsResponse;
import com.green.yp.api.apitype.producer.UserCredentialsRequest;
import com.green.yp.auth.model.AuthServiceResponse;
import com.green.yp.exception.ErrorCodeType;
import com.green.yp.exception.SystemException;
import com.green.yp.exception.UserCredentialsException;
import com.inversoft.error.Errors;
import com.inversoft.rest.ClientResponse;
import io.fusionauth.client.FusionAuthClient;
import io.fusionauth.domain.User;
import io.fusionauth.domain.UserRegistration;
import io.fusionauth.domain.api.UserRequest;
import io.fusionauth.domain.api.UserResponse;
import io.fusionauth.domain.api.user.RegistrationRequest;
import io.fusionauth.domain.api.user.RegistrationResponse;
import io.fusionauth.domain.api.user.SearchRequest;
import io.fusionauth.domain.api.user.SearchResponse;
import io.fusionauth.domain.search.UserSearchCriteria;
import jakarta.validation.constraints.NotNull;

import java.net.UnknownHostException;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FusionAuthService implements AuthenticationService {

  final FusionAuthClient fusionAuthClient;

  @Value("${fusionauth.green.yp.app.id}")
  private String applicationId;

  public FusionAuthService(FusionAuthClient fusionAuthClient) {
    this.fusionAuthClient = fusionAuthClient;
  }

  @Override
  public AuthServiceResponse<RegistrationResponse> registerUser(
      @NotNull @NonNull UUID producerId,
      @NotNull @NonNull UUID contactId,
      @NotNull @NonNull Boolean subscriberAdmin,
      @NotNull @NonNull UserCredentialsRequest userCredentialsRequest) {

    log.info("Create/Register new user credentials for producer : {}", producerId);

    User user = createFusionAuthUser(producerId, contactId, userCredentialsRequest);

    // Instantiate the user registration and request object
    UserRegistration registration = new UserRegistration();
    registration.applicationId = UUID.fromString(applicationId);
    registration
        .with(r -> r.username = user.username)
        .with(r -> r.roles.add(AccountRoleType.SUBSCRIBER.getRole()));
    if (subscriberAdmin) {
      registration.with(r -> r.roles.add(AccountRoleType.SUBSCRIBER_ADMIN.getRole()));
    }

    RegistrationRequest request = new RegistrationRequest(user, registration);

    // Use the returned ClientResponse object
    ClientResponse<RegistrationResponse, Errors> response =
        fusionAuthClient.register(null, request);

    if (response.wasSuccessful()) {
      log.info("Created new user credentials for producer : {}", producerId);
      return new AuthServiceResponse<>(response.getSuccessResponse().registrationVerificationId, response.successResponse);
    } else {
      // Handle errors
      log.error(
          "Error occurred when create/Register new user credentials for producer : {}",
          response.errorResponse);
      throw new UserCredentialsException(
          "Error when attempting to create user", response.exception);
    }
  }

  @Override
  public void disableUser(@NotNull @NonNull String externalAuthorizationServiceRef) {

    UUID userId = UUID.fromString(externalAuthorizationServiceRef);

    // Using the returned ClientResponse object
    ClientResponse<Void, Errors> response = fusionAuthClient.deactivateUser(userId);

    if (response.wasSuccessful()) {
      log.info("Existing user successfully suspended");
    } else {
      // Handling errors
      log.error(
          "Error when attempting to suspend/disable user: {}",
          response.getErrorResponse().toString());
    }
  }

  public AuthServiceResponse<UserResponse> findUser(
      @NotNull @NonNull String externalAuthorizationServiceRef) {
    UUID userId = UUID.fromString(externalAuthorizationServiceRef);

    ClientResponse<UserResponse, Errors> response = fusionAuthClient.retrieveUser(userId);

    if (response.wasSuccessful()) {
      log.debug("Found fusionAuth credentials : {}", externalAuthorizationServiceRef);
      return new AuthServiceResponse<>("", response.successResponse);
    } else {
      if ( response.getException() instanceof UnknownHostException) {
          log.error("Could not contact fusionAuth service - {}", response.getException().getMessage());
          throw new SystemException("Could not contact fusionAuth service", HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodeType.SYSTEM_ERROR);
      }
        // Handle errors
      log.error(
          "Error occurred when retrieving credentials for {} : {}",
          externalAuthorizationServiceRef, response.getErrorResponse() != null ? response.getErrorResponse() : response.getException());

      throw new UserCredentialsException(
          "Error when retrieving fusion auth credentials", response.exception);
    }
  }

  @Override
  public Optional<AuthenticatedUserCredentialsResponse> findUser(@NonNull String userName,
                                                                 @NonNull String emailAddress) {

    UserSearchCriteria criteria = new UserSearchCriteria();
    criteria.queryString = String.format("email:\"%s\" OR username:\"%s\"", emailAddress, userName);

    // Build the search request, for example, search by username or email
    SearchRequest request = new SearchRequest(criteria);

    ClientResponse<SearchResponse, Errors> response = fusionAuthClient.searchUsersByQuery(request);
    if ( response.wasSuccessful()){
      log.warn("Found {} credentials for {} {}", response.successResponse.total, userName, emailAddress);
      return response.getSuccessResponse().users.stream()
              .filter( user -> user.username.equals(userName))
              .findFirst()
              .map(user -> AuthenticatedUserCredentialsResponse.builder()
                      .externalAuthorizationServiceRef(user.id.toString())
                      .userName(user.username)
                      .lastName(user.lastName)
                      .firstName(user.firstName)
                      .emailAddress(user.email)
                      .build());
    } else {
      // Handle errors
      log.error(
              "Error occurred while searching for username {} emailAddress {}, error : {}",
              userName, emailAddress, response.errorResponse.toString());
      throw new UserCredentialsException(
              "Error when retrieving fusion auth credentials", response.exception);
    }
  }

  public AuthServiceResponse<UserResponse> modifyUser(
      @NotNull @NonNull UUID producerId,
      @NotNull @NonNull String externalAuthorizationServiceRef,
      @NotNull @NonNull UserCredentialsRequest userCredentialsRequest) {

    AuthServiceResponse<UserResponse> userResponse = findUser(externalAuthorizationServiceRef);

    User user = createFusionAuthUser(producerId, null, userCredentialsRequest);

    UserRequest userRequest = new UserRequest(user);
    userRequest.applicationId = UUID.fromString(applicationId);

    ClientResponse<UserResponse, Errors> response =
        fusionAuthClient.updateUser(userResponse.getActualResponse().user.id, userRequest);

    if (response.wasSuccessful()) {
      log.debug("Updated fusion auth credentials for : {}", externalAuthorizationServiceRef);
      return new AuthServiceResponse<>("", response.successResponse);
    } else {
      // Handle errors
      log.error(
          "Error occurred when updating credentials for {} : {}",
          externalAuthorizationServiceRef,
          response.errorResponse.toString());
      throw new UserCredentialsException(
          "Error when updating fusion auth credentials", response.exception);
    }
  }

  public void removeUser(String externalAuthorizationServiceRef) {
    log.info("Remove/Revolk user from FusionAuth for {}", externalAuthorizationServiceRef);
    ClientResponse<Void, Errors> response =
        fusionAuthClient.deleteUser(UUID.fromString(externalAuthorizationServiceRef));
    if (response.wasSuccessful()) {
      log.debug("Deleted fusion auth credentials for : {}", externalAuthorizationServiceRef);
    } else if (response.getStatus() != HttpStatus.NOT_FOUND.value()) {
      // Handle errors
      log.error(
          "Error occurred when deleting credentials for {} : {}",
          externalAuthorizationServiceRef,
          response.errorResponse.toString());
      throw new UserCredentialsException(
          "Error deleting fusion auth credentials", response.exception);
    }
  }

  private User createFusionAuthUser(UUID producerId, UUID contactId, UserCredentialsRequest userCredentialsRequest) {

    String username =
        StringUtils.isNotBlank(userCredentialsRequest.userName())
            ? userCredentialsRequest.userName()
            : userCredentialsRequest.emailAddress();

    var user = new User()
        .with(u -> u.email = userCredentialsRequest.emailAddress())
        .with(u -> u.tenantId = UUID.fromString(applicationId))
        .with(u -> u.firstName = userCredentialsRequest.firstName())
        .with(u -> u.lastName = userCredentialsRequest.lastName())
        .with(
            u ->
                u.fullName =
                    userCredentialsRequest.firstName() + " " + userCredentialsRequest.lastName())
        .with(u -> u.mobilePhone = userCredentialsRequest.cellPhone())
        .with(u -> u.username = username)
        .with(u -> u.password = userCredentialsRequest.credentials());
    if ( producerId != null ){
      user = user.with(u -> u.data.put("producerId", producerId));
    }
    if ( contactId != null ){
      user = user.with( u -> u.data.put("contactId", contactId));
    }
    return user;
  }
}
