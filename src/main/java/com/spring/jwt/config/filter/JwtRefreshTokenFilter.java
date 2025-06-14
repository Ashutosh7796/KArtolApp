package com.spring.jwt.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.jwt.dto.RefreshTokenRequest;
import com.spring.jwt.exception.BaseException;
import com.spring.jwt.jwt.JwtConfig;
import com.spring.jwt.jwt.JwtService;
import com.spring.jwt.service.security.UserDetailsCustom;
import com.spring.jwt.service.security.UserDetailsServiceCustom;
import com.spring.jwt.utils.BaseResponseDTO;
import com.spring.jwt.utils.HelperUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class JwtRefreshTokenFilter extends AbstractAuthenticationProcessingFilter implements Ordered {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final UserDetailsServiceCustom userDetailsService;
    private final JwtConfig jwtConfig;
    
    // Cookie name for refresh token
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    public JwtRefreshTokenFilter(AuthenticationManager manager,
                                JwtConfig jwtConfig,
                                JwtService jwtService,
                                UserDetailsServiceCustom userDetailsService) {
        super(new AntPathRequestMatcher(jwtConfig.getRefreshUrl(), "POST"));
        setAuthenticationManager(manager);
        this.objectMapper = new ObjectMapper();
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.jwtConfig = jwtConfig;
    }

    @Override
    public int getOrder() {
        // Use a value that places it after the login filter but before other filters
        return Ordered.LOWEST_PRECEDENCE - 125;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        log.info("Start refresh token authentication");
        
        // Get refresh token from cookie, request body, or Authorization header
        String refreshToken = null;
        
        try {
            // First try to get from cookie
            refreshToken = getRefreshTokenFromCookie(request);
            if (refreshToken != null) {
                log.info("Found refresh token in cookie");
            }
            
            // If not in cookie, try request body
            if (refreshToken == null) {
                try {
                    // Check if there's content to read
                    if (request.getContentLength() > 0) {
                        RefreshTokenRequest refreshRequest = objectMapper.readValue(request.getInputStream(), RefreshTokenRequest.class);
                        refreshToken = refreshRequest.getRefreshToken();
                        log.info("Found refresh token in request body");
                    } else {
                        log.info("Request body is empty");
                    }
                } catch (Exception e) {
                    log.debug("Failed to parse request body: {}", e.getMessage());
                }
            }
            
            // If still not found, try Authorization header
            if (refreshToken == null) {
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    refreshToken = authHeader.substring(7);
                    log.info("Found refresh token in Authorization header");
                }
            }
            
            // If no token found anywhere
            if (refreshToken == null) {
                // Log all cookies for debugging
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    log.info("Available cookies:");
                    for (Cookie cookie : cookies) {
                        log.info("Cookie: {} = {}, Path: {}, HttpOnly: {}", 
                            cookie.getName(), 
                            cookie.getValue().substring(0, Math.min(10, cookie.getValue().length())) + "...",
                            cookie.getPath(),
                            cookie.isHttpOnly());
                    }
                } else {
                    log.info("No cookies found in request");
                }
                
                // Log all headers for debugging
                log.info("Available headers:");
                Enumeration<String> headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    log.info("Header: {} = {}", headerName, request.getHeader(headerName));
                }
                
                throw new BadCredentialsException("No refresh token provided");
            }
            
            // Validate the refresh token
            if (!jwtService.isRefreshToken(refreshToken)) {
                throw new BadCredentialsException("Invalid token type - not a refresh token");
            }
            
            if (!jwtService.isValidToken(refreshToken)) {
                throw new BadCredentialsException("Expired or invalid refresh token");
            }
            
            // Extract username from the refresh token
            Claims claims = jwtService.extractClaims(refreshToken);
            String username = claims.getSubject();
            
            // Create authentication object
            RefreshTokenAuthentication auth = new RefreshTokenAuthentication(username, refreshToken);
            auth.setAuthenticated(true);
            
            return auth;
        } catch (Exception e) {
            log.error("Error processing refresh token: {}", e.getMessage());
            throw new BadCredentialsException(e.getMessage());
        }
    }
    
    /**
     * Get refresh token from cookie
     */
    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> refreshTokenCookie = Arrays.stream(cookies)
                .filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .findFirst();
            
            if (refreshTokenCookie.isPresent()) {
                log.debug("Found refresh token in cookie");
                return refreshTokenCookie.get().getValue();
            }
        }
        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                           Authentication authResult) throws IOException, ServletException {
        try {
            log.info("Processing successful refresh token authentication");
            String username = authResult.getName();
            String refreshToken = ((RefreshTokenAuthentication) authResult).getRefreshToken();
            
            // Get user details
            UserDetailsCustom userDetails = (UserDetailsCustom) userDetailsService.loadUserByUsername(username);
            
            // Generate device fingerprint
            String deviceFingerprint = jwtService.generateDeviceFingerprint(request);
            
            // Generate new tokens
            String newAccessToken = jwtService.generateToken(userDetails, deviceFingerprint);
            String newRefreshToken = jwtService.generateRefreshToken(userDetails, deviceFingerprint);
            
            // Create secure HttpOnly cookie for refresh token
            Cookie refreshTokenCookie = createRefreshTokenCookie(newRefreshToken);
            response.addCookie(refreshTokenCookie);
            
            // Create response object with only access token
            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", newAccessToken);
            // Don't include refresh token in response body anymore
            // tokens.put("refreshToken", newRefreshToken);
            
            String json = HelperUtils.JSON_WRITER.writeValueAsString(tokens);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write(json);
            log.info("End successful refresh token authentication");
        } catch (Exception ex) {
            log.error("Error during token refresh: {}", ex.getMessage());
            unsuccessfulAuthentication(request, response, new BadCredentialsException(ex.getMessage()));
        }
    }
    
    /**
     * Create a secure HttpOnly cookie for the refresh token
     */
    private Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Only send over HTTPS
        cookie.setPath("/"); // Make cookie available for all paths
        
        // Calculate max age from refresh token expiration (in seconds)
        cookie.setMaxAge(jwtConfig.getRefreshExpiration());
        
        // Add SameSite attribute
        cookie.setAttribute("SameSite", "Strict");
        
        return cookie;
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, 
                                             AuthenticationException failed) throws IOException, ServletException {
        // Clear the invalid refresh token cookie if present
        Cookie invalidCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");
        invalidCookie.setMaxAge(0); // Expire immediately
        invalidCookie.setPath(jwtConfig.getRefreshUrl());
        response.addCookie(invalidCookie);
        
        BaseResponseDTO responseDTO = new BaseResponseDTO();
        responseDTO.setCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        responseDTO.setMessage(failed.getMessage());

        String json = HelperUtils.JSON_WRITER.writeValueAsString(responseDTO);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json);
    }
    
    // Simple Authentication class for refresh tokens
    public static class RefreshTokenAuthentication implements Authentication {
        private final String username;
        private final String refreshToken;
        private boolean authenticated = false;
        
        public RefreshTokenAuthentication(String username, String refreshToken) {
            this.username = username;
            this.refreshToken = refreshToken;
        }
        
        @Override
        public Object getCredentials() {
            return refreshToken;
        }
        
        @Override
        public Object getPrincipal() {
            return username;
        }
        
        @Override
        public boolean isAuthenticated() {
            return authenticated;
        }
        
        @Override
        public void setAuthenticated(boolean isAuthenticated) {
            this.authenticated = isAuthenticated;
        }
        
        @Override
        public String getName() {
            return username;
        }
        
        public String getRefreshToken() {
            return refreshToken;
        }
        
        @Override
        public Object getDetails() {
            return null;
        }
        
        @Override
        public java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
            return java.util.Collections.emptyList();
        }
    }
} 