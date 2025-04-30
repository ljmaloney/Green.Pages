package com.green.yp.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Using https://cache2k.org/docs/latest/user-guide.html#spring as an in-memory cache */
@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public CacheManager cacheManager() {
    return new SpringCache2kCacheManager()
        .defaultSetup(
            builder ->
                builder
                    .permitNullValues(false)
                    .expireAfterWrite(Duration.of(30L, ChronoUnit.MINUTES))
                    .eternal(false));
  }
}
