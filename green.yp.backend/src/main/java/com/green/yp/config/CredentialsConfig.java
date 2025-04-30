package com.green.yp.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration("greenyp.credentials.config")
@Getter
public class CredentialsConfig {
  @Value("${max.attempts.counter:10}")
  private Integer maxAttemptsCounter;

  @Value("${lock.timeout.minutes:30}")
  private Integer timeLockMinutes;
}
