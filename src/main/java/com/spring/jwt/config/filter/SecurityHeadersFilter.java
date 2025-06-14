package com.spring.jwt.config.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter to add security headers that aren't directly configurable
 * in the current Spring Security version
 */
@Component
public class SecurityHeadersFilter implements Filter, Ordered {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        
        // Add Content-Security-Policy header to prevent XSS attacks
        httpResponse.setHeader("Content-Security-Policy", 
            "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
            "style-src 'self' 'unsafe-inline'; img-src 'self' data:; " +
            "font-src 'self'; connect-src 'self'; frame-src 'self'; " +
            "object-src 'none'; base-uri 'self'");
        
        // Add X-Content-Type-Options header to prevent MIME type sniffing
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        
        // Add X-Frame-Options header to prevent clickjacking
        httpResponse.setHeader("X-Frame-Options", "DENY");
        
        // Add X-XSS-Protection header as an additional layer of XSS protection
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
        
        // Add Strict-Transport-Security header to enforce HTTPS
        httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        
        // Add Cache-Control header to prevent caching of sensitive information
        httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        httpResponse.setHeader("Pragma", "no-cache");

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {
    }
    
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 50;
    }
} 