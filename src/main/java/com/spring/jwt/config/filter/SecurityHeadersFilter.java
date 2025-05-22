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
        
        // Add Referrer-Policy header
        httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        
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
        // High priority - run early in the filter chain
        return Ordered.HIGHEST_PRECEDENCE + 50;
    }
} 