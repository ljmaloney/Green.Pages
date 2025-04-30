package com.green.yp.auth.service;

import com.green.yp.api.apitype.producer.UserCredentialsRequest;
import com.green.yp.auth.model.AuthServiceResponse;
import io.fusionauth.domain.api.UserResponse;
import io.fusionauth.domain.api.user.RegistrationResponse;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {

  AuthServiceResponse<RegistrationResponse> registerUser(
      @NotNull @NonNull UUID producerId,
      Boolean subscriberAdmin,
      UserCredentialsRequest userCredentialsRequest);

  void disableUser(@NotNull @NonNull String externalAuthorizationServiceRef);

  AuthServiceResponse<UserResponse> findUser(
      @NotNull @NonNull String externalAuthorizationServiceRef);

  AuthServiceResponse<UserResponse> modifyUser(
      @NotNull @NonNull UUID producerId,
      @NonNull @NotNull String externalAuthorizationServiceRef,
      @NotNull @NonNull UserCredentialsRequest userCredentialsRequest);
}
