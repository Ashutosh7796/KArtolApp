package com.spring.jwt.jwt;

import com.spring.jwt.service.security.UserDetailsCustom;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Key;
import java.util.Map;

public interface JwtService {

    Claims extractClaims(String token);

    Key getKey();

    String generateToken(UserDetailsCustom userDetailsCustom);

    String generateToken(UserDetailsCustom userDetailsCustom, String deviceFingerprint);

    String generateRefreshToken(UserDetailsCustom userDetailsCustom, String deviceFingerprint);

    String extractDeviceFingerprint(String token);

    boolean isValidToken(String token, String deviceFingerprint);

    boolean isValidToken(String token);

    boolean isRefreshToken(String token);

    String generateDeviceFingerprint(HttpServletRequest request);

    Map<String, Object> extractAllCustomClaims(String token);
}
