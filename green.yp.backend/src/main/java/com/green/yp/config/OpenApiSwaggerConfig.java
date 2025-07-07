package com.green.yp.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(title = "Greenyp API", version = "v1"),
        servers = {
                @Server(url = "http://localhost:8081", description = "Local"),
                @Server(url = "https://services.greenyp.com", description = "Production"),
                @Server(url = "https://greenyp-service-api-lb-1807917553.us-east-1.elb.amazonaws.com", description = "AWS Load Balancer")
        }
)
public class OpenApiSwaggerConfig {
  @Value("${springdoc.oAuthFlow.authorization-url}")
  private String oauthUrl;

  @Value("${springdoc.oAuthFlow.token-url}")
  private String tokenUrl;

  @Bean
  public OpenAPI customOpenAPI() {
    log.info("Configure swagger for OAUTH security using FusionAuth");
    return new OpenAPI()
        .info(new Info().title("GreenYP API").version("v1"))
        .components(
            new Components()
                .addSecuritySchemes(
                    "OAuth2",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.OAUTH2)
                        .flows(
                            new OAuthFlows()
                                .authorizationCode(
                                    new OAuthFlow()
                                        .authorizationUrl(oauthUrl)
                                        .tokenUrl(tokenUrl)
                                        .scopes(
                                            new Scopes()
                                                .addString("openid", "OpenID scope")
                                                .addString("profile", "Profile scope"))))))
        .addSecurityItem(new SecurityRequirement().addList("OAuth2"));
  }
}
