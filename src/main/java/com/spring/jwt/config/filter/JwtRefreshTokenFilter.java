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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtRefreshTokenFilter extends AbstractAuthenticationProcessingFilter implements Ordered {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final UserDetailsServiceCustom userDetailsService;

    public JwtRefreshTokenFilter(AuthenticationManager manager,
                                JwtConfig jwtConfig,
                                JwtService jwtService,
                                UserDetailsServiceCustom userDetailsService) {
        super(new AntPathRequestMatcher(jwtConfig.getRefreshUrl(), "POST"));
        setAuthenticationManager(manager);
        this.objectMapper = new ObjectMapper();
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
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
        
        // Get refresh token from request body or Authorization header
        String refreshToken;
        
        try {
            // Try to read from request body first
            RefreshTokenRequest refreshRequest;
            try {
                refreshRequest = objectMapper.readValue(request.getInputStream(), RefreshTokenRequest.class);
                refreshToken = refreshRequest.getRefreshToken();
            } catch (Exception e) {
                log.debug("Failed to parse request body, checking Authorization header");
                // If request body parsing fails, try to get token from Authorization header
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    refreshToken = authHeader.substring(7);
                } else {
                    throw new BadCredentialsException("No refresh token provided");
                }
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
            
            // Create response object
            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", newAccessToken);
            tokens.put("refreshToken", newRefreshToken);
            
            String json = HelperUtils.JSON_WRITER.writeValueAsString(tokens);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write(json);
            log.info("End successful refresh token authentication");
        } catch (Exception ex) {
            log.error("Error during token refresh: {}", ex.getMessage());
            unsuccessfulAuthentication(request, response, new BadCredentialsException(ex.getMessage()));
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, 
                                             AuthenticationException failed) throws IOException, ServletException {
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