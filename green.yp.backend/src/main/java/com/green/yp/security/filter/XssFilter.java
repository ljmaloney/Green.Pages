package com.green.yp.security.filter;

import com.green.yp.security.XSSRequestWrapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Order(1)
public class XssFilter extends HttpFilter implements Filter {

  private static final List<String> EXCLUDED_URL_LIST = List.of("/v3/api-docs", "/swagger-ui/*");

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    log.info("########## Initiating Custom filter ##########");
    super.init(filterConfig);
  }

  @Override
  public void doFilter(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterchain)
      throws IOException, ServletException {

    final String path = request.getServletPath();

    if (EXCLUDED_URL_LIST.stream().anyMatch(path::startsWith)) {

      log.debug("Ignoring XSSContentFilter for {}", path);
      filterchain.doFilter(request, response);
    } else {
      log.debug("Sanitizing content using XSSRequestWrapper", path);
      filterchain.doFilter(new XSSRequestWrapper(request), response);
    }
  }

  @Override
  public void destroy() {
    super.destroy();
  }
}
