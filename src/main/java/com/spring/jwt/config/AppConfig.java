package com.spring.jwt.config;

import com.spring.jwt.config.filter.CustomAuthenticationProvider;
import com.spring.jwt.config.filter.JwtRefreshTokenFilter;
import com.spring.jwt.config.filter.JwtTokenAuthenticationFilter;
import com.spring.jwt.config.filter.JwtUsernamePasswordAuthenticationFilter;
import com.spring.jwt.config.filter.RateLimitingFilter;
import com.spring.jwt.config.filter.SecurityHeadersFilter;
import com.spring.jwt.config.filter.SqlInjectionFilter;
import com.spring.jwt.config.filter.XssFilter;
import com.spring.jwt.jwt.JwtConfig;
import com.spring.jwt.jwt.JwtService;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.service.security.UserDetailsServiceCustom;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class AppConfig {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private JwtConfig jwtConfig;
    
    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;
    
    @Autowired
    private SecurityHeadersFilter securityHeadersFilter;
    
    @Autowired
    private XssFilter xssFilter;
    
    @Autowired
    private SqlInjectionFilter sqlInjectionFilter;
    
    @Autowired
    private RateLimitingFilter rateLimitingFilter;
    
    @Value("${app.url.frontend:http://localhost:5173}")
    private String frontendUrl;
    
    @Value("#{'${app.cors.allowed-origins:http://localhost:5173,http://localhost:3000,http://localhost:8080}'.split(',')}")
    private List<String> allowedOrigins;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsServiceCustom userDetailsService() {
        return new UserDetailsServiceCustom(userRepository);
    }

    @Bean
    public JwtRefreshTokenFilter jwtRefreshTokenFilter(
            AuthenticationManager authenticationManager,
            JwtConfig jwtConfig,
            JwtService jwtService,
            UserDetailsServiceCustom userDetailsService) {
        return new JwtRefreshTokenFilter(authenticationManager, jwtConfig, jwtService, userDetailsService);
    }

    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
       requestHandler.setCsrfRequestAttributeName("_csrf");
        
        http.csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(requestHandler)
                .ignoringRequestMatchers(
                    "/api/auth/**", 
                    jwtConfig.getUrl(), 
                    jwtConfig.getRefreshUrl()
                )
            );

        http.cors(Customizer.withDefaults());

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.headers(headers -> headers
                .xssProtection(xss -> xss
                    .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self'; connect-src 'self'"))
                .frameOptions(frame -> frame.deny())
                .referrerPolicy(referrer -> referrer
                    .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                .permissionsPolicy(permissions -> permissions
                    .policy("camera=(), microphone=(), geolocation=()"))
            );

        http.authorizeHttpRequests(authorize -> authorize

                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(jwtConfig.getUrl()).permitAll()
                .requestMatchers(jwtConfig.getRefreshUrl()).permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/assessments/**").permitAll()
                .requestMatchers("/user/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/users/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").authenticated()
                .requestMatchers("/fees").permitAll()
                .requestMatchers("/questions").permitAll()
                .requestMatchers("/assessments").permitAll()
                .requestMatchers(
                        "/api/v1/auth/**",
                        "/v2/api-docs",
                        "/v3/api-docs",
                        "/v*/a*-docs/**",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/swagger-ui.html"
                ).permitAll()

                .anyRequest().authenticated());

        JwtUsernamePasswordAuthenticationFilter jwtUsernamePasswordAuthenticationFilter = new JwtUsernamePasswordAuthenticationFilter(authenticationManager(http), jwtConfig, jwtService, userRepository);
        JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter = new JwtTokenAuthenticationFilter(jwtConfig, jwtService, userDetailsService());
        JwtRefreshTokenFilter jwtRefreshTokenFilter = new JwtRefreshTokenFilter(authenticationManager(http), jwtConfig, jwtService, userDetailsService());
        
        http.addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(xssFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(sqlInjectionFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(securityHeadersFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(jwtRefreshTokenFilter, JwtUsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(jwtTokenAuthenticationFilter, JwtRefreshTokenFilter.class);

        http.authenticationProvider(customAuthenticationProvider);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(allowedOrigins);
                config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                config.setAllowCredentials(true); // Important for cookies
                config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
                config.setExposedHeaders(Arrays.asList("Authorization"));
                config.setMaxAge(3600L);
                return config;
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService())
               .passwordEncoder(passwordEncoder());
        return builder.build();
    }
}
