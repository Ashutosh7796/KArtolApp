# Spring Boot Security Implementation Guide

This guide provides a comprehensive explanation of the security implementation in this Spring Boot application. It is designed to help developers understand and implement the complete security setup from scratch.

## Table of Contents

1. [Overview](#overview)
2. [Core Components](#core-components)
3. [JWT Authentication](#jwt-authentication)
4. [Security Configuration](#security-configuration)
5. [Filters](#filters)
6. [CORS and CSRF Protection](#cors-and-csrf-protection)
7. [Rate Limiting](#rate-limiting)
8. [Input Validation and Sanitization](#input-validation-and-sanitization)
9. [SSL Configuration](#ssl-configuration)
10. [Password Management](#password-management)
11. [Implementation Steps](#implementation-steps)

## Overview

The security implementation in this application uses a layered approach with:

- JWT-based authentication
- Role-based authorization
- Request filtering for various security concerns
- Input validation and sanitization
- Rate limiting to prevent abuse
- Secure headers configuration
- SSL/TLS support

This implementation follows industry best practices and provides comprehensive protection against common web application vulnerabilities.

## Core Components

The security implementation consists of these core components:

| Component | Files | Purpose |
|-----------|-------|---------|
| Security Configuration | `AppConfig.java` | Main security configuration class defining security rules |
| JWT Service | `JwtConfig.java`, `JwtService.java`, `JwtServiceImpl.java` | JWT token generation, validation, and configuration |
| Authentication Filters | `JwtTokenAuthenticationFilter.java`, `JwtUsernamePasswordAuthenticationFilter.java`, `JwtRefreshTokenFilter.java` | Filter chain for authentication |
| User Details | `UserDetailsCustom.java`, `UserDetailsServiceCustom.java` | Custom user details implementation |
| Security Headers | `SecurityHeadersFilter.java`, `SecurityHeadersConfig.java` | HTTP security headers configuration |
| Input Validation | `XssFilter.java`, `SqlInjectionFilter.java` | Input sanitization and validation |
| Rate Limiting | `RateLimitingFilter.java`, `RateLimitingConfig.java`, `RateLimitingAspect.java` | Request rate limiting |
| Encryption | `EncryptionUtil.java`, `StringEncryptConverter.java` | Data encryption utilities |
| SSL Configuration | `SslConfig.java` | SSL/TLS configuration |

## JWT Authentication

JWT (JSON Web Token) authentication is implemented with the following components:

### 1. JWT Configuration (`JwtConfig.java`)

This class defines JWT-related settings:

```java
@Configuration
@ConfigurationProperties(prefix = "app.security.jwt")
@Data
public class JwtConfig {
    private String secretKey = "secure-jwt-key-with-minimum-512-bits-length-for-hs512";
    private String tokenPrefix = "Bearer ";
    private Integer tokenExpirationAfterDays = 14;
    private Integer refreshTokenExpirationAfterDays = 30;
    private String headerString = "Authorization";
    private String url = "/api/auth/login";
    private String refreshUrl = "/api/auth/refresh";
}
```

### 2. JWT Service Interface (`JwtService.java`)

Defines the contract for JWT operations:

```java
public interface JwtService {
    String generateToken(Authentication authentication);
    String generateRefreshToken(Authentication authentication);
    String generateTokenFromRefreshToken(String refreshToken);
    boolean validateToken(String token);
    Claims extractAllClaims(String token);
    String extractUsername(String token);
    // Additional methods as needed
}
```

### 3. JWT Service Implementation (`JwtServiceImpl.java`)

Implements token generation, validation, and parsing:

```java
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtConfig jwtConfig;
    
    @Override
    public String generateToken(Authentication authentication) {
        // Implementation details for generating a JWT access token
    }
    
    @Override
    public String generateRefreshToken(Authentication authentication) {
        // Implementation details for generating a JWT refresh token
    }
    
    @Override
    public boolean validateToken(String token) {
        // Implementation details for validating a JWT token
    }
    
    // Other method implementations
}
```

### 4. Token Blacklist Service (`TokenBlacklistService.java`)

Handles revoked tokens:

```java
@Service
public class TokenBlacklistService {
    private final Set<String> blacklistedTokens = Collections.synchronizedSet(new HashSet<>());
    
    public void blacklist(String token) {
        blacklistedTokens.add(token);
    }
    
    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}
```

## Security Configuration

The main security configuration is defined in `AppConfig.java`. This class:

1. Configures the security filter chain
2. Sets up authentication providers
3. Defines URL-based security rules
4. Configures CORS, CSRF, and other security features

### AppConfig.java Structure

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true,
    prePostEnabled = true
)
@Slf4j
public class AppConfig {

    // Dependencies via @Autowired
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configure CSRF protection
        http.csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .ignoringRequestMatchers(
                "/api/**",
                "/user/**",
                // Other public endpoints
            )
        );

        // Configure CORS
        http.cors(Customizer.withDefaults());

        // Configure session management
        http.sessionManagement(session -> 
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Configure security headers
        http.headers(headers -> headers
            .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
            .contentSecurityPolicy(csp -> csp.policyDirectives(
                "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self'; connect-src 'self'"))
            .frameOptions(frame -> frame.deny())
            .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
            .permissionsPolicy(permissions -> permissions.policy("camera=(), microphone=(), geolocation=()"))
        );

        // Configure URL-based security rules
        http.authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/api/v1/users/register").permitAll()
            .requestMatchers("/api/v1/users/password/**").permitAll()
            // More rules...
            .anyRequest().authenticated()
        );

        // Configure centralized public URL matcher
        RequestMatcher publicUrls = new OrRequestMatcher(
            new AntPathRequestMatcher("/api/auth/**"),
            new AntPathRequestMatcher("/api/public/**"),
            // More public URL patterns...
        );

        // Configure security filters
        JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter = 
            new JwtTokenAuthenticationFilter(jwtConfig, jwtService, userDetailsService(), publicUrls);
            
        // Add filters in the correct order
        http.addFilterBefore(jwtTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtRefreshTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(xssFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(sqlInjectionFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(securityHeadersFilter, UsernamePasswordAuthenticationFilter.class);

        // Configure authentication provider
        http.authenticationProvider(customAuthenticationProvider);
        
        return http.build();
    }

    // Other @Bean methods for CORS, authentication manager, etc.
}
```

### Key Configuration Methods

1. **Password Encoder**
   ```java
   @Bean
   public BCryptPasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder();
   }
   ```

2. **UserDetailsService**
   ```java
   @Bean
   public UserDetailsServiceCustom userDetailsService() {
       return new UserDetailsServiceCustom(userRepository);
   }
   ```

3. **CORS Configuration**
   ```java
   @Bean
   public CorsConfigurationSource corsConfigurationSource() {
       return new CorsConfigurationSource() {
           @Override
           public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
               CorsConfiguration config = new CorsConfiguration();
               config.setAllowedOrigins(allowedOrigins);
               config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
               config.setAllowCredentials(true);
               config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
               config.setExposedHeaders(Arrays.asList("Authorization"));
               config.setMaxAge(3600L);
               return config;
           }
       };
   }
   ```

4. **Authentication Manager**
   ```java
   @Bean
   public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
       AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
       builder.userDetailsService(userDetailsService())
               .passwordEncoder(passwordEncoder());
       return builder.build();
   }
   ```

## Filters

Security filters are a critical part of the implementation. They process HTTP requests in a specific order:

### 1. JWT Token Authentication Filter (`JwtTokenAuthenticationFilter.java`)

This filter authenticates users based on the JWT token in the request header:

```java
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final RequestMatcher publicUrls;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, 
            HttpServletResponse response, 
            FilterChain filterChain) throws ServletException, IOException {
        
        // Skip filter for public URLs
        if (publicUrls.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT token from header
        String authorizationHeader = request.getHeader(jwtConfig.getHeaderString());
        
        if (authorizationHeader == null || !authorizationHeader.startsWith(jwtConfig.getTokenPrefix())) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Extract and validate token
        String token = authorizationHeader.replace(jwtConfig.getTokenPrefix(), "");
        
        try {
            if (jwtService.validateToken(token)) {
                String username = jwtService.extractUsername(token);
                
                // Load user details and create authentication token
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null, 
                        userDetails.getAuthorities()
                    );
                
                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            // Handle exception
        }
        
        filterChain.doFilter(request, response);
    }
}
```

### 2. JWT Username/Password Authentication Filter (`JwtUsernamePasswordAuthenticationFilter.java`)

This filter handles username/password authentication and generates JWT tokens:

```java
public class JwtUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, 
            HttpServletResponse response) throws AuthenticationException {
        
        try {
            // Parse login request
            LoginRequest loginRequest = new ObjectMapper()
                .readValue(request.getInputStream(), LoginRequest.class);
            
            // Attempt authentication
            return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword(),
                    Collections.emptyList()
                )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        
        // Generate tokens
        String accessToken = jwtService.generateToken(authResult);
        String refreshToken = jwtService.generateRefreshToken(authResult);
        
        // Add tokens to response
        response.addHeader(jwtConfig.getHeaderString(), jwtConfig.getTokenPrefix() + accessToken);
        
        // Send response body with tokens
        TokenResponse tokenResponse = new TokenResponse(accessToken, refreshToken);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokenResponse);
    }
}
```

### 3. JWT Refresh Token Filter (`JwtRefreshTokenFilter.java`)

This filter handles token refresh requests:

```java
public class JwtRefreshTokenFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        // Only process the refresh token endpoint
        if (!request.getServletPath().equals(jwtConfig.getRefreshUrl())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Parse refresh token request
            RefreshTokenRequest refreshRequest = new ObjectMapper()
                .readValue(request.getInputStream(), RefreshTokenRequest.class);
            
            String refreshToken = refreshRequest.getRefreshToken();
            
            // Validate refresh token and generate new access token
            if (jwtService.validateToken(refreshToken)) {
                String newAccessToken = jwtService.generateTokenFromRefreshToken(refreshToken);
                
                // Send response with new access token
                TokenResponse tokenResponse = new TokenResponse(newAccessToken, refreshToken);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokenResponse);
                return;
            }
        } catch (Exception e) {
            // Handle exception
        }
        
        filterChain.doFilter(request, response);
    }
}
```

### 4. Security Headers Filter (`SecurityHeadersFilter.java`)

This filter adds security-related HTTP headers:

```java
public class SecurityHeadersFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        // Add security headers
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        
        filterChain.doFilter(request, response);
    }
}
```

### 5. XSS Filter (`XssFilter.java`)

This filter sanitizes input to prevent Cross-Site Scripting attacks:

```java
public class XssFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        // Wrap the request to sanitize parameters
        XssRequestWrapper wrappedRequest = new XssRequestWrapper(request);
        filterChain.doFilter(wrappedRequest, response);
    }
    
    private static class XssRequestWrapper extends HttpServletRequestWrapper {
        // Implementation of request wrapper with sanitization logic
    }
}
```

### 6. SQL Injection Filter (`SqlInjectionFilter.java`)

This filter detects and blocks SQL injection attempts:

```java
public class SqlInjectionFilter extends OncePerRequestFilter {

    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "('(''|[^'])*')|"  // Single quoted strings
        + "(\"(\"\"|[^\"])*\")" // Double quoted strings
        + "|(--[^\\r\\n]*)" // SQL comments
        + "|(union\\s+select)" // UNION SELECT attacks
        + "|(exec\\s+xp_)" // Stored procedure attacks
        + "|(;\\s*[^\\s])" // Statement terminator followed by new statement
        // Add more patterns as needed
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        // Check parameters for SQL injection patterns
        Map<String, String[]> params = request.getParameterMap();
        for (String[] values : params.values()) {
            for (String value : values) {
                if (isSqlInjectionDetected(value)) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "SQL injection detected");
                    return;
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isSqlInjectionDetected(String value) {
        return SQL_INJECTION_PATTERN.matcher(value).find();
    }
}
```

### 7. Rate Limiting Filter (`RateLimitingFilter.java`)

This filter prevents abuse by limiting request rates:

```java
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimiter rateLimiter;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        String clientIp = getClientIp(request);
        
        if (!rateLimiter.tryAcquire(clientIp)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Rate limit exceeded. Please try again later.");
            return;
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
```

## CORS and CSRF Protection

### CORS Configuration

Cross-Origin Resource Sharing (CORS) is configured in the `corsConfigurationSource` bean in `AppConfig.java`. The configuration:

1. Allows specific origins defined in application properties
2. Permits necessary HTTP methods
3. Allows credentials for authentication
4. Sets specific allowed and exposed headers
5. Sets a maximum age for preflight requests

```java
@Value("#{'${app.cors.allowed-origins:http://localhost:5173,http://localhost:3000,http://localhost:8080}'.split(',')}")
private List<String> allowedOrigins;

@Bean
public CorsConfigurationSource corsConfigurationSource() {
    return new CorsConfigurationSource() {
        @Override
        public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(allowedOrigins);
            config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            config.setAllowCredentials(true);
            config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
            config.setExposedHeaders(Arrays.asList("Authorization"));
            config.setMaxAge(3600L);
            return config;
        }
    };
}
```

Property configuration in `application.properties`:

```properties
# CORS configuration
app.cors.allowed-origins=http://localhost:5173,http://localhost:3000,http://localhost:8080
```

### CSRF Protection

CSRF protection is configured in the `securityFilterChain` bean in `AppConfig.java`:

```java
http.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    .ignoringRequestMatchers(
        "/api/**",
        "/user/**",
        // Other public endpoints
    )
);
```

This configuration:

1. Uses `CookieCsrfTokenRepository` to store CSRF tokens in cookies
2. Makes cookies accessible to JavaScript (`withHttpOnlyFalse()`)
3. Ignores CSRF protection for specific endpoints, typically stateless API endpoints

## Rate Limiting

Rate limiting prevents abuse by limiting the number of requests a client can make in a given time period.

### Rate Limiting Configuration

Rate limiting is configured in `RateLimitingConfig.java`:

```java
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "app.rate-limiting")
@Data
public class RateLimitingConfig {
    private boolean enabled = true;
    private int limitForPeriod = 100;
    private int refreshPeriod = 60;
    private int timeoutDuration = 300;
}
```

Property configuration in `application.properties`:

```properties
# Rate limiting settings
app.rate-limiting.enabled=true
app.rate-limiting.limit-for-period=100
app.rate-limiting.refresh-period=60
app.rate-limiting.timeout-duration=300
```

### Rate Limiter Implementation

A custom rate limiter implementation using Guava's `RateLimiter`:

```java
@Component
public class RateLimiter {
    private final Map<String, com.google.common.util.concurrent.RateLimiter> limiters = new ConcurrentHashMap<>();
    private final RateLimitingConfig config;
    
    public RateLimiter(RateLimitingConfig config) {
        this.config = config;
    }
    
    public boolean tryAcquire(String key) {
        if (!config.isEnabled()) {
            return true;
        }
        
        com.google.common.util.concurrent.RateLimiter limiter = limiters.computeIfAbsent(
            key, 
            k -> com.google.common.util.concurrent.RateLimiter.create(config.getLimitForPeriod() / (double) config.getRefreshPeriod())
        );
        
        return limiter.tryAcquire();
    }
}
```

### Rate Limiting Aspect

For method-level rate limiting using annotations:

```java
@Aspect
@Component
@ConditionalOnProperty(name = "app.rate-limiting.enabled", havingValue = "true", matchIfMissing = true)
public class RateLimitingAspect {
    
    private final RateLimiter rateLimiter;
    
    @Around("@annotation(rateLimit)")
    public Object limitRate(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String key = determineKey(joinPoint, rateLimit);
        
        if (!rateLimiter.tryAcquire(key)) {
            throw new TooManyRequestsException("Rate limit exceeded");
        }
        
        return joinPoint.proceed();
    }
    
    private String determineKey(ProceedingJoinPoint joinPoint, RateLimit rateLimit) {
        // Implementation to determine the rate limiting key
    }
}
```

## Input Validation and Sanitization

Input validation and sanitization protect against injection attacks like XSS and SQL injection.

### Bean Validation

Bean validation using Jakarta Bean Validation annotations:

```java
public class UserDTO {
    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", 
             message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character")
    private String password;
    
    // Other fields and methods
}
```

### Controller Validation

Validation in controllers:

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody UserDTO userDTO) {
        // Implementation
    }
    
    // Other endpoints
}
```

### XSS Sanitization

The `XssRequestWrapper` in `XssFilter.java` sanitizes input:

```java
private static class XssRequestWrapper extends HttpServletRequestWrapper {
    
    public XssRequestWrapper(HttpServletRequest request) {
        super(request);
    }
    
    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = sanitize(values[i]);
        }
        
        return encodedValues;
    }
    
    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        return sanitize(value);
    }
    
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return sanitize(value);
    }
    
    private String sanitize(String input) {
        if (input == null) {
            return null;
        }
        
        // Replace potentially harmful characters
        String sanitized = input
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll("\"", "&quot;")
            .replaceAll("'", "&#x27;")
            .replaceAll("\\(", "&#40;")
            .replaceAll("\\)", "&#41;")
            .replaceAll("/", "&#x2F;");
        
        return sanitized;
    }
}
```

### SQL Injection Prevention

SQL injection prevention strategies:

1. Using prepared statements in repositories:

```java
@Query("SELECT u FROM User u WHERE u.username = :username")
Optional<User> findByUsername(@Param("username") String username);
```

2. Using the `SqlInjectionFilter` to detect and block SQL injection attempts:

```java
private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
    "('(''|[^'])*')|"  // Single quoted strings
    + "(\"(\"\"|[^\"])*\")" // Double quoted strings
    + "|(--[^\\r\\n]*)" // SQL comments
    + "|(union\\s+select)" // UNION SELECT attacks
    + "|(exec\\s+xp_)" // Stored procedure attacks
    + "|(;\\s*[^\\s])" // Statement terminator followed by new statement
    // Add more patterns as needed
);
```

### Data Masking

Sensitive data masking using `DataMaskingUtils.java`:

```java
public class DataMaskingUtils {
    
    public static String maskEmail(String email) {
        if (email == null || email.length() <= 4) {
            return email;
        }
        
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
        }
        
        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        
        int visibleChars = Math.min(2, username.length());
        String maskedUsername = username.substring(0, visibleChars) 
            + "*".repeat(username.length() - visibleChars);
        
        return maskedUsername + domain;
    }
    
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() <= 4) {
            return phone;
        }
        
        int visibleDigits = 4;
        return "*".repeat(phone.length() - visibleDigits) 
            + phone.substring(phone.length() - visibleDigits);
    }
    
    // Other masking methods
}
```

## SSL Configuration

SSL/TLS configuration is managed by the `SslConfig.java` class:

```java
@Configuration
@Slf4j
public class SslConfig {

    @Value("${app.ssl.enabled:false}")
    private boolean sslEnabled;

    @PostConstruct
    public void init() {
        if (sslEnabled) {
            log.info("SSL is enabled by configuration");
        } else {
            log.info("SSL is disabled by configuration (server.ssl.enabled=false)");
        }
    }
}
```

SSL properties in `application.properties`:

```properties
# SSL configuration
app.ssl.enabled=false
server.ssl.enabled=false
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=yourpassword
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=youralias
```

### Keystore Initialization

The `KeystoreInitializer.java` class automatically sets up the keystore during application startup:

```java
@Component
@Slf4j
public class KeystoreInitializer implements CommandLineRunner {

    private static final String KEYSTORE_PATH = "src/main/resources/keystore.p12";
    private static final String KEYSTORE_PASSWORD = "yourpassword";
    private static final String KEY_ALIAS = "youralias";
    private static final String TARGET_PATH = "target/classes/keystore.p12";

    @Override
    public void run(String... args) throws Exception {
        File keystoreFile = new File(KEYSTORE_PATH);
        
        if (!keystoreFile.exists()) {
            log.info("Keystore not found, generating at: {}", KEYSTORE_PATH);
            generateKeystore();
        } else {
            log.info("Keystore already exists at: {}", KEYSTORE_PATH);
        }
        
        // Copy keystore to classpath for runtime access
        File targetDir = new File("target/classes");
        if (targetDir.exists()) {
            Files.copy(
                keystoreFile.toPath(), 
                Paths.get(TARGET_PATH), 
                StandardCopyOption.REPLACE_EXISTING
            );
            log.info("Keystore copied to classpath at: {}", TARGET_PATH);
        }
    }
    
    private void generateKeystore() throws Exception {
        // Implementation details for generating a keystore
        // This would typically use KeyStore API or a ProcessBuilder to run keytool
    }
}
```

## Password Management

The application implements industry-standard password management practices:

### Password Encoding

Passwords are encoded using BCrypt:

```java
@Bean
public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

### Password Reset Flow

The password reset flow is implemented in the `UserController`:

```java
@PostMapping("/password/forgot")
public ResponseEntity<ApiResponse> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
    // Generate and send reset token
}

@PostMapping("/password/reset")
public ResponseEntity<ApiResponse> resetPassword(@RequestBody @Valid ResetPassword resetPassword) {
    // Validate token and update password
}
```

The `ResetPassword` DTO with validation:

```java
@Data
public class ResetPassword {
    @NotBlank(message = "Token is required")
    private String token;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", 
             message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character")
    private String password;
    
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}
```

### Password Reset Email Template

Password reset emails use HTML templates (`src/main/resources/templates/reset-password.html`):

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Password Reset</title>
</head>
<body>
    <div>
        <h2>Password Reset Request</h2>
        <p>Dear {{username}},</p>
        <p>You have requested to reset your password. Please click the link below to reset your password:</p>
        <p>
            <a href="{{resetUrl}}">Reset Password</a>
        </p>
        <p>This link will expire in {{expiryTime}} minutes.</p>
        <p>If you did not request a password reset, please ignore this email.</p>
        <p>Regards,<br>The Team</p>
    </div>
</body>
</html>
```

### Email Service for Password Reset

The `EmailService` handles sending password reset emails:

```java
@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private SpringTemplateEngine templateEngine;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.url.password-reset}")
    private String passwordResetBaseUrl;
    
    @Async
    public void sendPasswordResetEmail(String to, String username, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Password Reset Request");
            
            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("resetUrl", passwordResetBaseUrl + "?token=" + token);
            context.setVariable("expiryTime", "30");
            
            String htmlContent = templateEngine.process("reset-password", context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Password reset email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send password reset email", e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
}
```

## Implementation Steps

To implement the complete security setup from scratch, follow these steps:

1. **Add Dependencies**

   Add these dependencies to your `pom.xml`:

   ```xml
   <!-- Spring Security -->
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-security</artifactId>
   </dependency>
   
   <!-- JWT -->
   <dependency>
       <groupId>io.jsonwebtoken</groupId>
       <artifactId>jjwt-api</artifactId>
       <version>0.11.5</version>
   </dependency>
   <dependency>
       <groupId>io.jsonwebtoken</groupId>
       <artifactId>jjwt-impl</artifactId>
       <version>0.11.5</version>
   </dependency>
   <dependency>
       <groupId>io.jsonwebtoken</groupId>
       <artifactId>jjwt-jackson</artifactId>
       <version>0.11.5</version>
   </dependency>
   
   <!-- Validation -->
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-validation</artifactId>
   </dependency>
   
   <!-- For Rate Limiting -->
   <dependency>
       <groupId>com.google.guava</groupId>
       <artifactId>guava</artifactId>
       <version>31.1-jre</version>
   </dependency>
   
   <!-- For Data Encryption -->
   <dependency>
       <groupId>com.github.ulisesbocchio</groupId>
       <artifactId>jasypt-spring-boot-starter</artifactId>
       <version>3.0.5</version>
   </dependency>
   ```

2. **Configure Application Properties**

   Set up security-related properties in `application.properties`:

   ```properties
   # Rate limiting settings
   app.rate-limiting.enabled=true
   app.rate-limiting.limit-for-period=100
   app.rate-limiting.refresh-period=60
   app.rate-limiting.timeout-duration=300
   
   # CORS configuration
   app.cors.allowed-origins=http://localhost:5173,http://localhost:3000,http://localhost:8080
   
   # SSL configuration
   app.ssl.enabled=false
   server.ssl.enabled=false
   server.ssl.key-store=classpath:keystore.p12
   server.ssl.key-store-password=yourpassword
   server.ssl.key-store-type=PKCS12
   server.ssl.key-alias=youralias
   
   # Encryption settings
   app.encryption.secret-key=secure-encryption-key-123456789012
   app.encryption.debug=true
   
   # Password reset URL
   app.url.password-reset=http://localhost:3000/reset-password
   ```

3. **Create Core Security Classes**

   Implement these core security components in order:

   1. `JwtConfig.java` - JWT configuration
   2. `JwtService.java` and `JwtServiceImpl.java` - JWT service
   3. `TokenBlacklistService.java` - Token blacklist
   4. `UserDetailsCustom.java` and `UserDetailsServiceCustom.java` - Custom user details
   5. Security filters (in this order):
      - `SecurityHeadersFilter.java`
      - `XssFilter.java`
      - `SqlInjectionFilter.java`
      - `RateLimitingFilter.java`
      - `JwtTokenAuthenticationFilter.java`
      - `JwtUsernamePasswordAuthenticationFilter.java`
      - `JwtRefreshTokenFilter.java`
   6. `AppConfig.java` - Main security configuration

4. **Implement Input Validation**

   Add validation to DTOs and controllers using Jakarta Bean Validation annotations.

5. **Set Up SSL**

   Implement `SslConfig.java` and `KeystoreInitializer.java` for SSL/TLS support.

6. **Implement Password Management**

   Set up password encoding, reset flow, and email service.

7. **Configure Rate Limiting**

   Implement `RateLimitingConfig.java`, `RateLimiter.java`, and `RateLimitingAspect.java`.

8. **Set Up Data Encryption**

   Implement `EncryptionUtil.java` and `StringEncryptConverter.java` for sensitive data encryption.

9. **Test Security Implementation**

   Create comprehensive tests for all security components.
