package com.green.yp.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Resilience4JConfig {

  private static final String GEOCODE_API_CONFIG = "geocode-api";

  @Bean
  public RateLimiterRegistry rateLimiterRegistry() {
    return RateLimiterRegistry.ofDefaults();
  }

  @Bean
  public CircuitBreakerRegistry circuitBreakerRegistry() {
    return CircuitBreakerRegistry.ofDefaults();
  }

  @Bean(name = "geocodeApiRateLimiter")
  public RateLimiter geocodeApiRateLimiter(RateLimiterRegistry registry) {
    return registry.rateLimiter(GEOCODE_API_CONFIG);
  }

  @Bean(name = "geocodeApiCircuitBreaker")
  public CircuitBreaker geocodeApiCircuitBreaker(CircuitBreakerRegistry registry) {
    return registry.circuitBreaker(GEOCODE_API_CONFIG);
  }
}
