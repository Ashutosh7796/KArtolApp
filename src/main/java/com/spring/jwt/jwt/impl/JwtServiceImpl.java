package com.spring.jwt.jwt.impl;

import com.spring.jwt.entity.User;
import com.spring.jwt.exception.BaseException;
import com.spring.jwt.jwt.JwtConfig;
import com.spring.jwt.jwt.JwtService;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.service.security.UserDetailsCustom;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {
    private static final String CLAIM_KEY_DEVICE_FINGERPRINT = "dfp";
    private static final String CLAIM_KEY_TOKEN_TYPE = "type";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";

    private final UserRepository userRepository;
    private final JwtConfig jwtConfig;
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtServiceImpl(@Lazy JwtConfig jwtConfig, UserDetailsService userDetailsService,
                          UserRepository userRepository) {
        this.jwtConfig = jwtConfig;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    public Claims extractClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public Key getKey() {
        byte[] key = Decoders.BASE64.decode(jwtConfig.getSecret());
        return Keys.hmacShaKeyFor(key);
    }

    @Override
    public String generateToken(UserDetailsCustom userDetailsCustom) {
        return generateToken(userDetailsCustom, null);
    }

    @Override
    public String generateToken(UserDetailsCustom userDetailsCustom, String deviceFingerprint) {
        Instant now = Instant.now();
        Instant notBefore = now.plusSeconds(jwtConfig.getNotBefore());

        List<String> roles = userDetailsCustom.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        log.info("Roles: {}", roles);

        Integer userId = null;
        String firstName = userDetailsCustom.getFirstName();

        if (roles.contains("USER") || roles.contains("ADMIN")) {
            userId = userDetailsCustom.getUserId();
        }
        
        log.debug("Generating access token for user: {}, device: {}", 
                userDetailsCustom.getUsername(), 
                deviceFingerprint != null ? deviceFingerprint.substring(0, 8) + "..." : "none");

        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(userDetailsCustom.getUsername())
                .setIssuer(jwtConfig.getIssuer())
                .setAudience(jwtConfig.getAudience())
                .setId(UUID.randomUUID().toString())
                .claim("firstname", firstName)
                .claim("userId", userId)
                .claim("authorities", roles)
                .claim("roles", roles)
                .claim("isEnable", userDetailsCustom.isEnabled())
                .claim(CLAIM_KEY_TOKEN_TYPE, TOKEN_TYPE_ACCESS)
                .setIssuedAt(Date.from(now))
                .setNotBefore(Date.from(notBefore))
                .setExpiration(Date.from(now.plusSeconds(jwtConfig.getExpiration())))
                .signWith(getKey(), SignatureAlgorithm.HS256);

        if (jwtConfig.isDeviceFingerprintingEnabled() && StringUtils.hasText(deviceFingerprint)) {
            jwtBuilder.claim(CLAIM_KEY_DEVICE_FINGERPRINT, deviceFingerprint);
        }

        return jwtBuilder.compact();
    }
    
    @Override
    public String generateRefreshToken(UserDetailsCustom userDetailsCustom, String deviceFingerprint) {
        Instant now = Instant.now();
        Instant notBefore = now.plusSeconds(jwtConfig.getNotBefore());
        
        log.debug("Generating refresh token for user: {}, device: {}", 
                userDetailsCustom.getUsername(), 
                deviceFingerprint != null ? deviceFingerprint.substring(0, 8) + "..." : "none");

        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(userDetailsCustom.getUsername())
                .setIssuer(jwtConfig.getIssuer())
                .setId(UUID.randomUUID().toString())
                .claim(CLAIM_KEY_TOKEN_TYPE, TOKEN_TYPE_REFRESH)
                .setIssuedAt(Date.from(now))
                .setNotBefore(Date.from(notBefore))
                .setExpiration(Date.from(now.plusSeconds(jwtConfig.getRefreshExpiration())))
                .signWith(getKey(), SignatureAlgorithm.HS256);

        if (jwtConfig.isDeviceFingerprintingEnabled() && StringUtils.hasText(deviceFingerprint)) {
            jwtBuilder.claim(CLAIM_KEY_DEVICE_FINGERPRINT, deviceFingerprint);
        }

        return jwtBuilder.compact();
    }
    
    @Override
    public String extractDeviceFingerprint(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.get(CLAIM_KEY_DEVICE_FINGERPRINT, String.class);
        } catch (Exception e) {
            log.warn("Error extracting device fingerprint from token", e);
            return null;
        }
    }
    
    @Override
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return TOKEN_TYPE_REFRESH.equals(claims.get(CLAIM_KEY_TOKEN_TYPE, String.class));
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public String generateDeviceFingerprint(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        
        try {
            StringBuilder deviceInfo = new StringBuilder();
            deviceInfo.append(request.getHeader("User-Agent")).append("|");
            deviceInfo.append(request.getRemoteAddr()).append("|");
            deviceInfo.append(request.getHeader("Accept-Language")).append("|");
            deviceInfo.append(request.getHeader("Accept-Encoding"));

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(deviceInfo.toString().getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Error generating device fingerprint", e);
            return null;
        }
    }
    
    @Override
    public Map<String, Object> extractAllCustomClaims(String token) {
        Claims claims = extractClaims(token);

        Map<String, Object> customClaims = new HashMap<>(claims);
        customClaims.remove("sub");
        customClaims.remove("iat");
        customClaims.remove("exp");
        customClaims.remove("jti");
        customClaims.remove("iss");
        customClaims.remove("aud");
        customClaims.remove("nbf");
        
        return customClaims;
    }

    @Override
    public boolean isValidToken(String token) {
        return isValidToken(token, null);
    }
    
    @Override
    public boolean isValidToken(String token, String deviceFingerprint) {
        try {
            final String username = extractUsername(token);
            
            if (StringUtils.isEmpty(username)) {
                return false;
            }
    
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            if (ObjectUtils.isEmpty(userDetails)) {
                return false;
            }

            Claims claims = extractAllClaims(token);

            if (jwtConfig.isDeviceFingerprintingEnabled() && StringUtils.hasText(deviceFingerprint)) {
                String tokenDeviceFingerprint = claims.get(CLAIM_KEY_DEVICE_FINGERPRINT, String.class);

                if (StringUtils.hasText(tokenDeviceFingerprint) && !tokenDeviceFingerprint.equals(deviceFingerprint)) {
                    log.warn("Device fingerprint mismatch: token={}, request={}", 
                            tokenDeviceFingerprint.substring(0, 8) + "...", 
                            deviceFingerprint.substring(0, 8) + "...");
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String extractUsername(String token){
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction){
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    private Claims extractAllClaims(String token){
        Claims claims;

        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (ExpiredJwtException e){
            throw new BaseException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "Token expiration");
        }catch (UnsupportedJwtException e){
            throw new BaseException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "Token's not supported");
        }catch (MalformedJwtException e){
            throw new BaseException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "Invalid format 3 part of token");
        }catch (SignatureException e){
            throw new BaseException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "Invalid format token");
        }catch (Exception e){
            throw new BaseException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), e.getLocalizedMessage());
        }

        return claims;
    }
}


