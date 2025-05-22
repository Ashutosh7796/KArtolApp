package com.spring.jwt.config.filter;

import com.spring.jwt.jwt.JwtConfig;
import com.spring.jwt.jwt.JwtService;
import com.spring.jwt.utils.BaseResponseDTO;
import com.spring.jwt.utils.HelperUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter implements Ordered {

    private final JwtConfig jwtConfig;
    private final JwtService jwtService;
    private boolean setauthreq = true;

    @Override
    public int getOrder() {
        // Set to run after the UsernamePasswordAuthenticationFilter
        return Ordered.LOWEST_PRECEDENCE - 120;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        // Skip authentication for excluded paths
        if (requestUri.contains("/api/jwtUnAuthorize/block") || 
            requestUri.contains("/api/jwtUnAuthorize/Exclude")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!setauthreq) {
            handleAccessBlocked(response);
            return;
        }

        String accessToken = request.getHeader(jwtConfig.getHeader());

        log.debug("Processing request: {}", requestUri);

        if (!ObjectUtils.isEmpty(accessToken) && accessToken.startsWith(jwtConfig.getPrefix() + " ")) {
            accessToken = accessToken.substring((jwtConfig.getPrefix() + " ").length());

            try {
                // Generate device fingerprint if enabled
                String deviceFingerprint = null;
                if (jwtConfig.isDeviceFingerprintingEnabled()) {
                    deviceFingerprint = jwtService.generateDeviceFingerprint(request);
                    log.debug("Generated device fingerprint: {}", deviceFingerprint != null ? 
                            deviceFingerprint.substring(0, 8) + "..." : "none");
                }
                
                // Skip device fingerprint validation for refresh tokens
                boolean isRefreshToken = jwtService.isRefreshToken(accessToken);
                
                // Check if token is valid (including device fingerprint check if not a refresh token)
                boolean tokenValid = isRefreshToken ? 
                        jwtService.isValidToken(accessToken) : 
                        jwtService.isValidToken(accessToken, deviceFingerprint);

                if (tokenValid) {
                    Claims claims = jwtService.extractClaims(accessToken);
                    String username = claims.getSubject();
                    String tokenType = claims.get("type", String.class);
                    
                    // Refresh tokens should not grant access to protected resources
                    if ("refresh".equals(tokenType)) {
                        log.warn("Attempt to use refresh token for resource access: {}", requestUri);
                        handleInvalidTokenType(response);
                        return;
                    }
                    
                    List<String> authorities = claims.get("authorities", List.class);

                    if (StringUtils.hasText(username)) {
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        username,
                                        null,
                                        authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                                );
                        
                        // Add device fingerprint to authentication details if available
                        if (deviceFingerprint != null) {
                            auth.setDetails(deviceFingerprint);
                        }
                        
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        log.debug("User authenticated: {}", username);
                    }
                } else {
                    log.warn("Invalid token for path: {}", requestUri);
                    handleInvalidToken(response);
                    return;
                }
            } catch (Exception e) {
                log.error("Authentication error for path {}: {}", requestUri, e.getMessage());
                handleAuthenticationException(response, e);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void handleAccessBlocked(HttpServletResponse response) throws IOException {
        BaseResponseDTO responseDTO = new BaseResponseDTO();
        responseDTO.setCode(String.valueOf(HttpStatus.SERVICE_UNAVAILABLE.value()));
        responseDTO.setMessage("d7324asdx8hg");

        String json = HelperUtils.JSON_WRITER.writeValueAsString(responseDTO);

        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json);
    }
    
    private void handleInvalidToken(HttpServletResponse response) throws IOException {
        BaseResponseDTO responseDTO = new BaseResponseDTO();
        responseDTO.setCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        responseDTO.setMessage("Invalid or expired token");

        String json = HelperUtils.JSON_WRITER.writeValueAsString(responseDTO);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json);
    }
    
    private void handleInvalidTokenType(HttpServletResponse response) throws IOException {
        BaseResponseDTO responseDTO = new BaseResponseDTO();
        responseDTO.setCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        responseDTO.setMessage("Invalid token type for this operation");

        String json = HelperUtils.JSON_WRITER.writeValueAsString(responseDTO);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json);
    }

    private void handleAuthenticationException(HttpServletResponse response, Exception e) throws IOException {
        BaseResponseDTO responseDTO = new BaseResponseDTO();
        responseDTO.setCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        responseDTO.setMessage("Authentication failed");

        String json = HelperUtils.JSON_WRITER.writeValueAsString(responseDTO);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json);
    }

    public void setauthreq(boolean setauthreq) {
        this.setauthreq = setauthreq;
    }
}
