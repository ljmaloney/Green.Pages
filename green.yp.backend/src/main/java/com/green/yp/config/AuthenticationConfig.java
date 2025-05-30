package com.green.yp.config;

import io.fusionauth.client.FusionAuthClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Component
@Slf4j
@ConfigurationProperties(prefix = "com.greenyp.auth")
public class AuthenticationConfig {
  @Value("${fusionauth.green.yp.apiKey}")
  private String apiKey;

  @Value("${fusionauth.green.yp.baseUrl}")
  private String baseUrl;

  @Value("${fusionauth.green.yp.host}")
  private String fusionHost;

  @Value("${fusionauth.green.yp.tenant}")
  private String tenantId;

  @Value("${fusionauth.green.yp.connectTimeout:2000}")
  private int connectTimeout;

  @Value("${fusionauth.green.yp.readTimeout:2000}")
  private int readTimeout;

  //    @Bean
  //    public SecurityFilterChain filterChain(HttpSecurity http, ClientRegistrationRepository repo)
  //            throws Exception {
  //
  //        var base_uri =
  // OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;
  //        var resolver = new DefaultOAuth2AuthorizationRequestResolver(repo, base_uri);
  //
  //
  // resolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());
  //
  //        http
  //                .authorizeRequests(a -> a
  //                        .antMatchers("/").permitAll()
  //                        .anyRequest().authenticated())
  //                .oauth2Login(login ->
  // login.authorizationEndpoint().authorizationRequestResolver(resolver));
  //
  //        http.logout(logout -> logout
  //                .logoutSuccessUrl("/"));
  //
  //        return http.build();
  //    }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public FusionAuthClient fusionAuthClient() {
    log.info(
        "configValue: fusionauth.baseUrl: {}, fusionauth.host: {}"
            + " fusionauth.bakktConsumerTenantId: {}",
        baseUrl,
        fusionHost,
        tenantId);
    return new FusionAuthClient(
        apiKey,
        StringUtils.isEmpty(fusionHost) ? baseUrl : fusionHost,
        connectTimeout,
        readTimeout,
        tenantId);
  }
}
