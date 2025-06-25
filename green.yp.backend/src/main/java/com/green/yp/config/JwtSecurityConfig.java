package com.green.yp.config;

import com.green.yp.auth.service.AuthenticationService;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Slf4j
@Configuration
public class JwtSecurityConfig {

  @Bean
  public JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withJwkSetUri("http://localhost:9011/.well-known/jwks.json").build();
  }

  @Bean
  public Converter<Jwt, AbstractAuthenticationToken> authenticationConverter(
      AuthenticationService authenticationService) {
    return new FusionAuthJwtAuthenticationConverter(authenticationService);
  }

  static class FusionAuthJwtAuthenticationConverter
      implements Converter<Jwt, AbstractAuthenticationToken> {

    private final AuthenticationService authenticationService;

    public FusionAuthJwtAuthenticationConverter(AuthenticationService authenticationService) {
      this.authenticationService = authenticationService;
    }

    public AbstractAuthenticationToken convert(Jwt jwt) {
      Set<SimpleGrantedAuthority> authorities = new HashSet<>();

      // Extract roles from token claims (optional)
      List<String> tokenRoles = jwt.getClaimAsStringList("roles");
      if (tokenRoles != null) {
        authorities.addAll(
            tokenRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toSet()));
      }

      // Add roles from FusionAuth user data via API
      String userId = jwt.getSubject();
      if (StringUtils.isNotBlank(userId)) {
        var userResponse = authenticationService.findUser(userId);
        log.info("user response from fusionAuth - {}", userResponse);
      }
      log.info("roles from jwt : {}", authorities);
      return new JwtAuthenticationToken(jwt, authorities);
    }
  }
}
