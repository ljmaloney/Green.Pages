package com.green.yp.config;

import com.squareup.square.AsyncSquareClient;
import com.squareup.square.SquareClient;
import com.squareup.square.core.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfig {
    @Value("${greenyp.payment.access-token}")
    private transient String paymentAccessToken;

    @Value("${greenyp.payment.environment}")
    private transient String environment;

    @Bean
    public SquareClient squareClient() {
        return SquareClient.builder()
                .token(paymentAccessToken)
                .environment(getEnvironment())
                .build();
    }

    @Bean
    public AsyncSquareClient asyncSquareClient() {
        return AsyncSquareClient.builder()
            .token(paymentAccessToken)
            .environment(getEnvironment())
            .build();
    }

    private Environment getEnvironment() {
        return switch (environment.toLowerCase()) {
            case "sandbox" -> Environment.SANDBOX;
            case "production" -> Environment.PRODUCTION;
            default -> Environment.SANDBOX;
        };
    }
}
