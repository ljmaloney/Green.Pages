package com.green.yp.auth.service;

import com.green.yp.api.apitype.account.AccountRoleType;
import com.green.yp.api.apitype.producer.UserCredentialsRequest;
import com.green.yp.auth.model.AuthServiceResponse;
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
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
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
      @NotNull @NonNull Boolean subscriberAdmin,
      @NotNull @NonNull UserCredentialsRequest userCredentialsRequest) {

    log.info("Create/Register new user credentials for producer : {}", producerId);

    User user = createFusionAuthUser(userCredentialsRequest);

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
      return new AuthServiceResponse<>("", response.successResponse);
    } else {
      // Handle errors
      log.error(
          "Error occurred when create/Register new user credentials for producer : {}",
          response.errorResponse.toString());
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
      // Handle errors
      log.error(
          "Error occurred when retrieving credentials for {} : {}",
          externalAuthorizationServiceRef,
          response.errorResponse.toString());
      throw new UserCredentialsException(
          "Error when retrieving fusion auth credentials", response.exception);
    }
  }

  public AuthServiceResponse<UserResponse> modifyUser(
      @NotNull @NonNull UUID producerId,
      @NotNull @NonNull String externalAuthorizationServiceRef,
      @NotNull @NonNull UserCredentialsRequest userCredentialsRequest) {

    AuthServiceResponse<UserResponse> userResponse = findUser(externalAuthorizationServiceRef);

    User user = createFusionAuthUser(userCredentialsRequest);

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
    ClientResponse<Void, Errors> response =
        fusionAuthClient.deleteUser(UUID.fromString(externalAuthorizationServiceRef));
    if (response.wasSuccessful()) {
      log.debug("Deleted fusion auth credentials for : {}", externalAuthorizationServiceRef);
    } else {
      // Handle errors
      log.error(
          "Error occurred when deleting credentials for {} : {}",
          externalAuthorizationServiceRef,
          response.errorResponse.toString());
      throw new UserCredentialsException(
          "Error deleting fusion auth credentials", response.exception);
    }
  }

  private User createFusionAuthUser(UserCredentialsRequest userCredentialsRequest) {

    String username =
        StringUtils.isNotBlank(userCredentialsRequest.userName())
            ? userCredentialsRequest.userName()
            : userCredentialsRequest.emailAddress();

    return new User()
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
  }
}
