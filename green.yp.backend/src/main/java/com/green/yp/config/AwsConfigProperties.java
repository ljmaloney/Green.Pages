package com.green.yp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "greenyp.cloud.aws")
public class AwsConfigProperties {
    private String impl;
    private S3 s3 = new S3();

    @Data
    public static class S3 {
        private boolean enabled = true;
        private String region = "us-east-1";
    }
}
