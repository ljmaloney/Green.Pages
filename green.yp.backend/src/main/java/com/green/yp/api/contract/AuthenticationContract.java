package com.green.yp.api.contract;

import com.green.yp.api.apitype.producer.UserCredentialsRequest;
import com.green.yp.auth.model.AuthServiceResponse;
import com.green.yp.auth.service.FusionAuthService;
import io.fusionauth.domain.api.UserResponse;
import io.fusionauth.domain.api.user.RegistrationResponse;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationContract {

  private final FusionAuthService authenticationService;

  public AuthenticationContract(FusionAuthService authenticationService) {
    this.authenticationService = authenticationService;
  }

  public AuthServiceResponse<RegistrationResponse> registerUser(
      @NotNull @NonNull UUID producerId,
      Boolean subscriberAdmin,
      @NotNull @NonNull UserCredentialsRequest credentialsRequest) {
    return authenticationService.registerUser(producerId, subscriberAdmin, credentialsRequest);
  }

  public void disableUser(String externalAuthorizationServiceRef) {
    authenticationService.disableUser(externalAuthorizationServiceRef);
  }

  public AuthServiceResponse<UserResponse> findUser(String externalAuthorizationServiceRef) {
    return authenticationService.findUser(externalAuthorizationServiceRef);
  }

  public AuthServiceResponse<UserResponse> modifyUser(
      UUID producerId, String externalServiceRef, UserCredentialsRequest userCredentialsRequest) {
    return authenticationService.modifyUser(producerId, externalServiceRef, userCredentialsRequest);
  }

  public void removeUser(String externalAuthorizationServiceRef) {
    authenticationService.removeUser(externalAuthorizationServiceRef);
  }
}
