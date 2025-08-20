package com.green.yp.util;

import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
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

    // Allow alphanumeric, dash, underscore, and dot
    private static final Pattern SAFE_FILENAME = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9._-]{0,254}$");

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
      log.error(e.getMessage(), e);
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

    public static boolean isInValidFileName(String filename) {
        if (filename == null || filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return true;
        }
        // Must match safe characters
        return !SAFE_FILENAME.matcher(filename).matches();
    }

}
