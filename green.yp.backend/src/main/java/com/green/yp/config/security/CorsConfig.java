package com.green.yp.config.security;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {

    var allowedOrigins =
        new String[] {
          "http://localhost:8080",
          "http://services.greenyp.com",
          "https://services.greenyp.com",
          "https://*.lovable.app"
        };

    var allowedMethods = new String[] {"GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"};

    var allowedHeaders = new String[] {"Content-Type", "Authorization"};

            registry
                .addMapping("/**")
                .allowedOriginPatterns(allowedOrigins)
                .allowedMethods(allowedMethods)
                .allowedHeaders(allowedHeaders)
                .allowCredentials(true);
  }
}
