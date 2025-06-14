package com.spring.jwt.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.jwt.dto.LoginRequest;
import com.spring.jwt.entity.User;
import com.spring.jwt.exception.BaseException;
import com.spring.jwt.jwt.JwtConfig;
import com.spring.jwt.jwt.JwtService;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.service.security.UserDetailsCustom;
import com.spring.jwt.utils.BaseResponseDTO;
import com.spring.jwt.utils.HelperUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class JwtUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter implements Ordered {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    public JwtUsernamePasswordAuthenticationFilter(AuthenticationManager manager,
                                                   JwtConfig jwtConfig,
                                                   JwtService jwtService,
                                                   UserRepository userRepository){
        super(new AntPathRequestMatcher(jwtConfig.getUrl(), "POST"));
        setAuthenticationManager(manager);
        this.objectMapper = new ObjectMapper();
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    public int getOrder() {
        // Use a standard value for the filter order (same as UsernamePasswordAuthenticationFilter)
        return Ordered.LOWEST_PRECEDENCE - 130;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        log.info("Start attempt to authentication");
        LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
        log.info("End attempt to authentication");

        return getAuthenticationManager()
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword(),
                        Collections.emptyList()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {
        try {
            UserDetailsCustom userDetailsCustom = (UserDetailsCustom) authentication.getPrincipal();
            List<String> roles = userDetailsCustom.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            
            // Generate device fingerprint
            String deviceFingerprint = jwtService.generateDeviceFingerprint(request);
            log.debug("Generated device fingerprint: {}", 
                    deviceFingerprint != null ? deviceFingerprint.substring(0, 8) + "..." : "none");
            
            // Save device fingerprint to user entity
            try {
                User user = userRepository.findByEmail(userDetailsCustom.getUsername());
                if (user != null) {
                    user.setDeviceFingerprint(deviceFingerprint);
                    user.setLastLogin(LocalDateTime.now());
                    userRepository.save(user);
                    log.debug("Saved device fingerprint for user: {}", user.getEmail());
                }
            } catch (Exception e) {
                log.error("Error saving device fingerprint: {}", e.getMessage(), e);
                // Continue even if saving fingerprint fails
            }

            // Generate tokens with device fingerprint
            String accessToken = jwtService.generateToken(userDetailsCustom, deviceFingerprint);
            String refreshToken = jwtService.generateRefreshToken(userDetailsCustom, deviceFingerprint);
            
            // Create response object
            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);
            
            String json = HelperUtils.JSON_WRITER.writeValueAsString(tokens);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write(json);
            log.info("End success authentication");
        } catch (BaseException ex) {
            log.error("Error during token generation: {}", ex.getMessage());
            unsuccessfulAuthentication(request, response, new BadCredentialsException(ex.getMessage()));
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        BaseResponseDTO responseDTO = new BaseResponseDTO();
        responseDTO.setCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        responseDTO.setMessage(failed.getMessage());

        String json = HelperUtils.JSON_WRITER.writeValueAsString(responseDTO);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json);
    }
}
