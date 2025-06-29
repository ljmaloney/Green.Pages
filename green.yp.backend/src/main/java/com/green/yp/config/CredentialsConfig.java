package com.green.yp.config;

import io.fusionauth.client.FusionAuthClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Getter
@Configuration
public class CredentialsConfig {

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

  @Value("${greenyp.credentials.config.max.attempts.counter:10}")
  private Integer maxAttemptsCounter;

  @Value("${greenyp.credentials.config.lock.timeout.minutes:30}")
  private Integer timeLockMinutes;

  @Bean
  public FusionAuthClient fusionAuthClient() {
    log.info(
        "configValue: fusionauth.baseUrl: {}, fusionauth.host: {}" + " fusionauth.tenantId: {}",
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
