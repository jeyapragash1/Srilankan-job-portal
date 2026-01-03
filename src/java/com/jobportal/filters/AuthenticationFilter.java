package com.jobportal.filters;

import com.jobportal.models.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter to ensure users are authenticated before accessing protected resources.
 */
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String loginURI = httpRequest.getContextPath() + "/login.jsp";
        String loginServletURI = httpRequest.getContextPath() + "/login";
        String registerURI = httpRequest.getContextPath() + "/register.jsp";
        String registerServletURI = httpRequest.getContextPath() + "/register";

        boolean loggedIn = (session != null && session.getAttribute("user") != null);
        boolean loginRequest = httpRequest.getRequestURI().equals(loginURI) || 
                               httpRequest.getRequestURI().equals(loginServletURI);
        boolean registerRequest = httpRequest.getRequestURI().equals(registerURI) || 
                                  httpRequest.getRequestURI().equals(registerServletURI);

        if (loggedIn || loginRequest || registerRequest) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(loginURI + "?error=Please log in to access this page");
        }
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
