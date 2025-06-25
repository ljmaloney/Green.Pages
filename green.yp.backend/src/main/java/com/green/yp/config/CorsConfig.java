package com.green.yp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/reference/**")
        .allowedOrigins(
            "http://localhost:8080",
            "http://services.greenyp.com",
            "https://services.greenyp.com",
            "https://*.lovable.app")
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        .allowedHeaders("Content-Type", "Authorization")
        .allowCredentials(true);
        registry.addMapping("/account/**")
                .allowedOrigins(
                        "http://localhost:8080",
                        "http://services.greenyp.com",
                        "https://services.greenyp.com",
                        "https://*.lovable.app")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization")
                .allowCredentials(true);
        registry.addMapping("/invoice/**")
                .allowedOrigins(
                        "http://localhost:8080",
                        "http://services.greenyp.com",
                        "https://services.greenyp.com",
                        "https://*.lovable.app")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization")
                .allowCredentials(true);
        registry.addMapping("/producer/**")
                .allowedOrigins(
                        "http://localhost:8080",
                        "http://services.greenyp.com",
                        "https://services.greenyp.com",
                        "https://*.lovable.app")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization")
                .allowCredentials(true);
        registry.addMapping("/profile/**")
                .allowedOrigins(
                        "http://localhost:8080",
                        "http://services.greenyp.com",
                        "https://services.greenyp.com",
                        "https://*.lovable.app")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization")
                .allowCredentials(true);
        registry.addMapping("/search/**")
                .allowedOrigins(
                        "http://localhost:8080",
                        "http://services.greenyp.com",
                        "https://services.greenyp.com",
                        "https://*.lovable.app")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization")
                .allowCredentials(true);
    }
}
