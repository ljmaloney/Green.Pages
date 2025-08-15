package com.green.yp.config.security;

import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Component
@Slf4j
@ConfigurationProperties(prefix = "com.greenyp.auth")
@EnableMethodSecurity(prePostEnabled = true)
public class AuthenticationConfig {

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http, Converter<Jwt, AbstractAuthenticationToken> authenticationConverter)
      throws Exception {
    http.cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.POST, "/account",
                                "/account/applyInitialPayment",
                                "/account/*/validate",
                                "/contact",
                                "/email/validate",
                                "/email/contact",
                                "/classified/create-ad").permitAll()
                    .requestMatchers(
                        "/",
                        "/classified/create-ad",
                        "/classified/**",
                        "/index.html",
                        "/favicon.ico",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/webjars/**")
                    .permitAll()
                    .requestMatchers(
                        HttpMethod.GET,
                        "/health/**",
                        "/index.html",
                        "/account/**",
                        "/account/user/**",
                        "/invoice/**",
                        "/payment/**",
                        "/reference/**",
                        "/producer/profiles",
                        "/producer/profile/**",
                        "/producer/*/gallery",
                        "/producer/*/location/*/services",
                        "/producer/*/location/*/products",
                        "/producer/location/product/**",
                        "/search",
                            "/v2/search")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(
            oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(authenticationConverter)));
    return http.build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(
        Arrays.asList(
            "http://localhost:8080",
            "http://localhost:8081",
            "https://services.greenyp.com",
            "https://*.greenyp.com",
            "https://greenyp.com",
            "https://greenyp-service-api-lb-1807917553.us-east-1.elb.amazonaws.com/",
            "https://*.lovable.app"));
    configuration.setAllowedMethods(
        Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "cache-control", "Cache-Control"));
    configuration.setExposedHeaders(List.of("Authorization"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public CorsFilter corsFilter(CorsConfigurationSource corsConfigurationSource) {
    return new CorsFilter(corsConfigurationSource);
  }
}
