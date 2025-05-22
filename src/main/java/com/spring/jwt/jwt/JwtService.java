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
    
    // Add method to generate token with device fingerprint
    String generateToken(UserDetailsCustom userDetailsCustom, String deviceFingerprint);
    
    // Add method to generate refresh token
    String generateRefreshToken(UserDetailsCustom userDetailsCustom, String deviceFingerprint);
    
    // Add method to extract device fingerprint from token
    String extractDeviceFingerprint(String token);
    
    // Add method to validate token including device fingerprint check
    boolean isValidToken(String token, String deviceFingerprint);

    boolean isValidToken(String token);
    
    // Add method to check if a token is a refresh token
    boolean isRefreshToken(String token);
    
    // Add method to generate device fingerprint from request
    String generateDeviceFingerprint(HttpServletRequest request);
    
    // Add method to extract all custom claims from token
    Map<String, Object> extractAllCustomClaims(String token);
}
