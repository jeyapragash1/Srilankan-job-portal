package com.jobportal.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * CSRF (Cross-Site Request Forgery) protection utility.
 */
public class CSRFUtil {
    
    private static final String CSRF_TOKEN_SESSION_ATTR = "csrfToken";
    private static final int TOKEN_LENGTH = 32;
    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates a new CSRF token and stores it in the session.
     *
     * @param session the HTTP session
     * @return the generated CSRF token
     */
    public static String generateToken(HttpSession session) {
        byte[] token = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(token);
        String tokenString = Base64.getUrlEncoder().encodeToString(token);
        session.setAttribute(CSRF_TOKEN_SESSION_ATTR, tokenString);
        return tokenString;
    }

    /**
     * Gets the CSRF token from the session, creating one if it doesn't exist.
     *
     * @param session the HTTP session
     * @return the CSRF token
     */
    public static String getToken(HttpSession session) {
        String token = (String) session.getAttribute(CSRF_TOKEN_SESSION_ATTR);
        if (token == null) {
            token = generateToken(session);
        }
        return token;
    }

    /**
     * Validates the CSRF token from the request against the session token.
     *
     * @param request the HTTP request
     * @return true if token is valid, false otherwise
     */
    public static boolean validateToken(HttpServletRequest request) {
        if (!ConfigManager.getBooleanProperty("security.csrf.enabled", true)) {
            return true; // CSRF protection disabled
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String sessionToken = (String) session.getAttribute(CSRF_TOKEN_SESSION_ATTR);
        String requestToken = request.getParameter("csrfToken");

        if (requestToken == null) {
            requestToken = request.getHeader("X-CSRF-Token");
        }

        return sessionToken != null && sessionToken.equals(requestToken);
    }

    /**
     * Invalidates the current CSRF token.
     *
     * @param session the HTTP session
     */
    public static void invalidateToken(HttpSession session) {
        if (session != null) {
            session.removeAttribute(CSRF_TOKEN_SESSION_ATTR);
        }
    }
}
