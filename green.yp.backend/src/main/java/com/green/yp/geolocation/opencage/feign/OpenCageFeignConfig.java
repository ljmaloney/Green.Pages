package com.green.yp.geolocation.opencage.feign;

import feign.Logger;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import feign.slf4j.Slf4jLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenCageFeignConfig {

    @Value("${greenyp.geocoder.retry.start:100}")
    private long retryStartMillis;
    @Value("${greenyp.geocoder.retry.max:100}")
    private long retryMaxMillis;
    @Value("${greenyp.geocoder.retry.maxAttempt:5}")
    private int retryMaxAttempt;

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC; // Or FULL for debugging
    }

    @Bean
    public Logger feignLogger() {
        return new Slf4jLogger();
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(retryStartMillis, retryMaxMillis, retryMaxAttempt);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new OpenCageErrorDecoder();
    }
}

