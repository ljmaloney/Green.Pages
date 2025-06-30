package com.green.yp.config.security;

import java.util.List;
import org.springframework.security.oauth2.jwt.Jwt;

public record AuthenticatedUser(String userId, String email, List<String> roles, Jwt jwt) {}
