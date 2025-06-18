package com.spring.jwt.config.filter;

import com.spring.jwt.jwt.JwtConfig;
import com.spring.jwt.jwt.JwtService;
import com.spring.jwt.service.security.UserDetailsCustom;
import com.spring.jwt.service.security.UserDetailsServiceCustom;
import com.spring.jwt.utils.BaseResponseDTO;
import com.spring.jwt.utils.HelperUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;
    private final JwtService jwtService;
    private final UserDetailsServiceCustom userDetailsService;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        log.debug("JWT Filter processing request for path: {}", path);

        if (isPublicEndpoint(path, request.getMethod())) {
            log.debug("Skipping JWT authentication for public endpoint: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String token = getJwtFromRequest(request);
        
        if (token == null) {
            log.warn("No JWT token found for protected path: {}", path);
            handleAccessDenied(response);
            return;
        }
        
        try {
            if (!processToken(token)) {
                log.warn("Invalid token for path: {}", path);
                handleInvalidToken(response);
                return;
            }

            filterChain.doFilter(request, response);
            
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            handleExpiredToken(response);
        } catch (JwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            handleInvalidToken(response);
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            handleAuthenticationException(response, e);
        }
    }

    /**
     * Process the JWT token and set authentication if valid
     * @return true if token is valid and authentication was set, false otherwise
     */
    private boolean processToken(String token) {
        if (jwtService.isValidToken(token)) {
            Claims claims = jwtService.extractClaims(token);
            String username = claims.getSubject();

            if (jwtService.isRefreshToken(token)) {
                log.warn("Refresh token used for API access - not allowed");
                return false;
            }
            
            if (!ObjectUtils.isEmpty(username)) {
                log.debug("Valid token found for user: {}", username);

                List<String> authorities = claims.get("authorities", List.class);
                if (authorities == null) {
                    authorities = claims.get("roles", List.class);
                }
                
                if (authorities != null) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                    );

                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.debug("Authentication set in security context for user: {}", username);
                    return true;
                } else {
                    log.warn("No authorities found in token for user: {}", username);
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Check if the endpoint is public and doesn't require authentication
     */
    private boolean isPublicEndpoint(String path, String method) {
        return (path.equals(jwtConfig.getRefreshUrl()) && "POST".equals(method)) || 
               (path.equals(jwtConfig.getUrl()) && "POST".equals(method)) ||
               path.startsWith("/user/") ||
               path.equals("/fees") ||
               path.equals("/questions/add") ||
               path.equals("/questions/search") ||
               path.startsWith("/api/public/") ||
               path.startsWith("/api/auth/") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/v2/api-docs") ||
               path.startsWith("/configuration/") ||
               path.startsWith("/webjars/") ||
               path.contains("swagger-ui.html");
    }

    /**
     * Extract JWT token from request (header or cookie)
     */
    private String getJwtFromRequest(HttpServletRequest request) {

        String bearerToken = request.getHeader(jwtConfig.getHeader());
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtConfig.getPrefix() + " ")) {
            log.debug("Found token in Authorization header");
            return bearerToken.substring((jwtConfig.getPrefix() + " ").length());
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {

            Optional<Cookie> accessTokenCookie = Arrays.stream(cookies)
                .filter(cookie -> ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .findFirst();
                
            if (accessTokenCookie.isPresent()) {
                log.debug("Found access token in cookie");
                return accessTokenCookie.get().getValue();
            }
        }
        
        return null;
    }
    
    private void handleAccessDenied(HttpServletResponse response) throws IOException {
        BaseResponseDTO responseDTO = new BaseResponseDTO();
        responseDTO.setCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        responseDTO.setMessage("Access denied: Authentication required");

        String json = HelperUtils.JSON_WRITER.writeValueAsString(responseDTO);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json);
    }
    
    private void handleInvalidToken(HttpServletResponse response) throws IOException {
        BaseResponseDTO responseDTO = new BaseResponseDTO();
        responseDTO.setCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        responseDTO.setMessage("Invalid token");

        String json = HelperUtils.JSON_WRITER.writeValueAsString(responseDTO);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json);
    }
    
    private void handleExpiredToken(HttpServletResponse response) throws IOException {
        BaseResponseDTO responseDTO = new BaseResponseDTO();
        responseDTO.setCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        responseDTO.setMessage("Expired token");

        String json = HelperUtils.JSON_WRITER.writeValueAsString(responseDTO);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json);
    }
    
    private void handleAuthenticationException(HttpServletResponse response, Exception e) throws IOException {
        BaseResponseDTO responseDTO = new BaseResponseDTO();
        responseDTO.setCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        responseDTO.setMessage("Authentication failed: " + e.getMessage());

        String json = HelperUtils.JSON_WRITER.writeValueAsString(responseDTO);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json);
    }
}
