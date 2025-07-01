package com.green.yp.config.security;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebUserConfig implements WebMvcConfigurer {
  private final AuthenticatedUserResolver resolver;

  public WebUserConfig(AuthenticatedUserResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(resolver);
  }
}
