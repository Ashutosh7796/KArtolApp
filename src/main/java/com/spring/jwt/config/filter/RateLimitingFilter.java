package com.spring.jwt.config.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Filter to prevent brute force attacks by implementing rate limiting
 */
@Component
public class RateLimitingFilter implements Filter, Ordered {

    // Maximum number of requests allowed in the window
    private static final int MAX_REQUESTS = 30;
    
    // Time window in seconds
    private static final long WINDOW_SIZE_IN_SECONDS = 60;
    
    // HTTP status code for Too Many Requests (429)
    private static final int STATUS_TOO_MANY_REQUESTS = 429;
    
    // Cache to store request counts per IP
    private final Map<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Skip rate limiting for non-sensitive endpoints
        String path = httpRequest.getRequestURI();
        if (isPublicEndpoint(path)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Get client IP
        String clientIp = getClientIp(httpRequest);
        
        // Check if the request exceeds the rate limit
        if (isRateLimitExceeded(clientIp)) {
            // Return 429 Too Many Requests
            httpResponse.setStatus(STATUS_TOO_MANY_REQUESTS);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"error\":\"Rate limit exceeded. Please try again later.\"}");
            return;
        }
        
        // Continue with the filter chain
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // No initialization needed
    }

    @Override
    public void destroy() {
        // No cleanup needed
    }
    
    @Override
    public int getOrder() {
        // Run before other security filters
        return Ordered.HIGHEST_PRECEDENCE + 30;
    }
    
    /**
     * Checks if the request from the given IP exceeds the rate limit
     */
    private boolean isRateLimitExceeded(String clientIp) {
        long now = System.currentTimeMillis();

        RequestCounter counter = requestCounts.computeIfAbsent(clientIp, k -> new RequestCounter());

        if (now - counter.getWindowStart() > TimeUnit.SECONDS.toMillis(WINDOW_SIZE_IN_SECONDS)) {
            counter.reset(now);
        }

        counter.incrementCount();

        return counter.getCount() > MAX_REQUESTS;
    }
    
    /**
     * Gets the client IP address from the request
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {

            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
    
    /**
     * Checks if the given path is a public endpoint that should not be rate limited
     */
    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/public/") || 
               path.startsWith("/api/auth/") || 
               path.equals("/api/auth/login") || 
               path.equals("/api/auth/refresh");
    }
    
    /**
     * Class to track request counts in a time window
     */
    private static class RequestCounter {
        private long windowStart;
        private int count;
        
        public RequestCounter() {
            this.windowStart = System.currentTimeMillis();
            this.count = 0;
        }
        
        public void reset(long timestamp) {
            this.windowStart = timestamp;
            this.count = 0;
        }
        
        public void incrementCount() {
            this.count++;
        }
        
        public int getCount() {
            return count;
        }
        
        public long getWindowStart() {
            return windowStart;
        }
    }
} 