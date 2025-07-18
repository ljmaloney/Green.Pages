package com.green.yp.config.security;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class WebUserConfig implements WebMvcConfigurer {
  private final AuthenticatedUserResolver resolver;

  public WebUserConfig(AuthenticatedUserResolver resolver) {
    log.debug("AuthenticatedUserResolver invoked");
    this.resolver = resolver;
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    log.debug("AuthenticatedUserResolver added");
    resolvers.add(resolver);
  }
}
