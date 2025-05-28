package com.green.yp.util;

import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestUtil {
  private static final String[] IP_HEADER_CANDIDATES = {
    "X-Forwarded-For",
    "Proxy-Client-IP",
    "WL-Proxy-Client-IP",
    "HTTP_X_FORWARDED_FOR",
    "HTTP_X_FORWARDED",
    "HTTP_X_CLUSTER_CLIENT_IP",
    "HTTP_CLIENT_IP",
    "HTTP_FORWARDED_FOR",
    "HTTP_FORWARDED",
    "HTTP_VIA",
    "REMOTE_ADDR"
  };

  private RequestUtil() {}

  public static String getRequestIP() {
    return getRequestIP(null);
  }

  public static String getRequestIP(HttpServletRequest request) {
    String ip = null;
    if (request == null) {
      if (RequestContextHolder.getRequestAttributes() == null) {
        return null;
      }
      request =
          ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    try {
      ip = InetAddress.getLocalHost().getHostAddress();
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (!StringUtils.isEmpty(ip)) return ip;

    for (String header : IP_HEADER_CANDIDATES) {
      String ipList = request.getHeader(header);
      if (StringUtils.isBlank(ipList) && !"unknown".equalsIgnoreCase(ipList)) {
        return ipList.split(",")[0];
      }
    }

    return request.getRemoteAddr();
  }
}