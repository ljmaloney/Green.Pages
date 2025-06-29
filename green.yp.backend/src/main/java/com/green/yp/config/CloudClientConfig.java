package com.green.yp.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.location.LocationClient;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class CloudClientConfig {
  @Bean
  @ConditionalOnProperty(
      prefix = "greenyp.cloud.aws.s3",
      name = "enabled",
      havingValue = "true",
      matchIfMissing = true)
  public S3Client s3Client(AwsConfigProperties props) {
    return S3Client.builder()
        .region(Region.of(props.getS3().getRegion()))
        .credentialsProvider(DefaultCredentialsProvider.create())
        .build();
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "greenyp.geocoder",
      name = "impl",
      havingValue = "aws",
      matchIfMissing = true)
  public LocationClient locationClient() {
    return LocationClient.builder()
        .region(Region.of("us-east-1")) // or your region
        .build();
  }
}
