package com.green.yp.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/** Using https://cache2k.org/docs/latest/user-guide.html#spring as an in-memory cache */
@Configuration
@EnableCaching
public class CacheManagerConfig {

  private final CacheConfig cacheConfig;

  public CacheManagerConfig(CacheConfig cacheConfig) {
    this.cacheConfig = cacheConfig;
  }

  @Bean
  @Primary
  public CacheManager cacheManager(){
    final SpringCache2kCacheManager cacheManager = new SpringCache2kCacheManager()
            .defaultSetup(
                    builder ->
                            builder
                                    .permitNullValues(false)
                                    .expireAfterWrite(Duration.of(30L, ChronoUnit.MINUTES))
                                    .eternal(false));

    Arrays.stream(CacheEnumeration.values()).forEach( cache ->
              cacheManager.addCaches(builder ->
                      builder.name(cache.cacheName)
                              .expireAfterWrite(cacheConfig.getExpireAfterWrite(cache), TimeUnit.MINUTES)
                              .idleScanTime(cacheConfig.getIdleScanTime(cache), TimeUnit.MINUTES)
                              .eternal(cacheConfig.getEternal(cache))
                              .permitNullValues(cacheConfig.permitNullValues(cache))));
    return cacheManager;
  }
}