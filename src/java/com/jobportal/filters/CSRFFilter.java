package com.jobportal.filters;

import com.jobportal.utils.CSRFUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter to protect against CSRF attacks.
 */
public class CSRFFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Only validate CSRF token for POST, PUT, DELETE requests
        String method = httpRequest.getMethod();
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || 
            "DELETE".equalsIgnoreCase(method)) {
            
            // Skip CSRF check for login and register
            String uri = httpRequest.getRequestURI();
            if (uri.endsWith("/login") || uri.endsWith("/register")) {
                chain.doFilter(request, response);
                return;
            }

            // Validate CSRF token
            if (!CSRFUtil.validateToken(httpRequest)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, 
                                      "Invalid CSRF token. Please refresh the page and try again.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
