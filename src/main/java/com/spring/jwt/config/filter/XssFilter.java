package com.spring.jwt.config.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Filter to protect against XSS attacks by sanitizing request parameters and form data
 */
@Component
public class XssFilter implements Filter, Ordered {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        XssRequestWrapper wrappedRequest = new XssRequestWrapper((HttpServletRequest) request);
        chain.doFilter(wrappedRequest, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {
    }
    
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 40;
    }
    
    /**
     * Request wrapper that sanitizes parameters to prevent XSS attacks
     */
    private static class XssRequestWrapper extends HttpServletRequestWrapper {
        
        // Patterns for common XSS attack vectors
        private static final Pattern[] XSS_PATTERNS = {
            // Script tags
            Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // JavaScript events
            Pattern.compile("on\\w+\\s*=\\s*\".*?\"", Pattern.CASE_INSENSITIVE),
            Pattern.compile("on\\w+\\s*=\\s*'.*?'", Pattern.CASE_INSENSITIVE),
            // Inline JavaScript
            Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
            // CSS expressions
            Pattern.compile("expression\\(.*?\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // CSS properties
            Pattern.compile("behavior\\s*:\\s*url\\(.*?\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // HTML tags
            Pattern.compile("<.*?\\s+.*?\\s*=.*?>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Eval
            Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Alert
            Pattern.compile("alert\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Document write
            Pattern.compile("document\\.write\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Document cookie
            Pattern.compile("document\\.cookie", Pattern.CASE_INSENSITIVE),
            // IFrame
            Pattern.compile("<iframe(.*?)>(.*?)</iframe>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Form
            Pattern.compile("<form(.*?)>(.*?)</form>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
        };
        
        // Cached parameters
        private Map<String, String[]> sanitizedParameterMap;

        public XssRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getParameter(String name) {
            String parameter = super.getParameter(name);
            return parameter != null ? sanitize(parameter) : null;
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values == null) {
                return null;
            }
            
            String[] sanitizedValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                sanitizedValues[i] = sanitize(values[i]);
            }
            
            return sanitizedValues;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            if (sanitizedParameterMap == null) {
                Map<String, String[]> rawParameterMap = super.getParameterMap();
                sanitizedParameterMap = new HashMap<>(rawParameterMap.size());
                
                for (Map.Entry<String, String[]> entry : rawParameterMap.entrySet()) {
                    String[] rawValues = entry.getValue();
                    String[] sanitizedValues = new String[rawValues.length];
                    
                    for (int i = 0; i < rawValues.length; i++) {
                        sanitizedValues[i] = sanitize(rawValues[i]);
                    }
                    
                    sanitizedParameterMap.put(entry.getKey(), sanitizedValues);
                }
            }
            
            return Collections.unmodifiableMap(sanitizedParameterMap);
        }

        @Override
        public String getHeader(String name) {
            String header = super.getHeader(name);
            return header != null ? sanitize(header) : null;
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            Enumeration<String> headers = super.getHeaders(name);
            if (headers == null) {
                return null;
            }
            
            // Sanitize each header value
            return new Enumeration<String>() {
                @Override
                public boolean hasMoreElements() {
                    return headers.hasMoreElements();
                }

                @Override
                public String nextElement() {
                    return sanitize(headers.nextElement());
                }
            };
        }

        /**
         * Sanitizes the given value to prevent XSS attacks
         */
        private String sanitize(String value) {
            if (value == null) {
                return null;
            }
            
            // Apply all XSS patterns
            String sanitizedValue = value;
            for (Pattern pattern : XSS_PATTERNS) {
                sanitizedValue = pattern.matcher(sanitizedValue).replaceAll("");
            }
            
            // Additional encoding for HTML entities
            sanitizedValue = sanitizedValue
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&#x27;")
                .replaceAll("&", "&amp;")
                .replaceAll("/", "&#x2F;");
            
            return sanitizedValue;
        }
    }
} 