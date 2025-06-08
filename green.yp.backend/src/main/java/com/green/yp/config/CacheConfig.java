package com.green.yp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "cache")
public class CacheConfig {

    private Map<String, CacheProperties> configs = new HashMap<>();

    public Map<String, CacheProperties> getConfigs() {
        return configs;
    }

    public void setConfigs(Map<String, CacheProperties> configs) {
        this.configs = configs;
    }

    public long getExpireAfterWrite(CacheEnumeration cache) {
        if ( configs.containsKey(cache.cacheName)){
            return configs.get(cache.cacheName).expirationMinutes != null ? configs.get(cache.cacheName).expirationMinutes : cache.expirationMinutes;
        }
        return cache.getExpirationMinutes();
    }

    public long getIdleScanTime(CacheEnumeration cache) {
        if ( configs.containsKey(cache.cacheName)){
            return configs.get(cache.cacheName).getIdleScanTime() != null ? configs.get(cache.cacheName).getIdleScanTime() : cache.getIdleScanTime();
        }
        return cache.getIdleScanTime();
    }

    public boolean getEternal(CacheEnumeration cache) {
        if ( configs.containsKey(cache.cacheName)){
            return configs.get(cache.cacheName).getEternal() != null ? configs.get(cache.cacheName).getEternal() : cache.isEternal();
        }
        return cache.isEternal();
    }

    public boolean permitNullValues(CacheEnumeration cache) {
        if ( configs.containsKey(cache.cacheName)){
            return configs.get(cache.cacheName).permitNullValues() != null ? configs.get(cache.cacheName).permitNullValues() : cache.isPermitNullValues();
        }
        return cache.isPermitNullValues();
    }

    @Data
    public class CacheProperties {
        private Boolean permitNullValues;
        private Long expirationMinutes;
        private Boolean eternal;
        private Long idleScanTime;

        public Boolean permitNullValues() {
            return permitNullValues;
        }
    }
}