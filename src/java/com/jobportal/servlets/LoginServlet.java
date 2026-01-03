package com.jobportal.servlets;

import com.jobportal.dao.UserDao;
import com.jobportal.dao.UserDaoImpl;
import com.jobportal.exception.AuthenticationException;
import com.jobportal.models.User;
import com.jobportal.utils.PasswordUtil;
import com.jobportal.utils.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Enhanced LoginServlet with BCrypt password verification and comprehensive security.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        userDao = new UserDaoImpl();
        logger.info("LoginServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Redirect GET requests to login page
        response.sendRedirect("login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            // Input validation
            if (!ValidationUtil.isNotBlank(email) || !ValidationUtil.isNotBlank(password)) {
                logger.warn("Login attempt with empty credentials");
                response.sendRedirect("login.jsp?error=Email and password are required");
                return;
            }

            if (!ValidationUtil.isValidEmail(email)) {
                logger.warn("Login attempt with invalid email format: {}", email);
                response.sendRedirect("login.jsp?error=Invalid email format");
                return;
            }

            // Sanitize input
            email = ValidationUtil.sanitizeForHTML(email.trim());

            // Authenticate user
            User user = userDao.getUserByEmail(email);

            if (user != null && PasswordUtil.verifyPassword(password, user.getPassword())) {
                // Successful authentication
                HttpSession session = request.getSession(true);
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getId());
                session.setAttribute("userRole", user.getRole());
                session.setMaxInactiveInterval(30 * 60); // 30 minutes
                
                logger.info("User logged in successfully: {} ({})", email, user.getRole());
                
                // Redirect based on role
                String redirectPage = getRedirectPage(user.getRole());
                response.sendRedirect(redirectPage);
                
            } else {
                // Authentication failed
                logger.warn("Failed login attempt for email: {}", email);
                response.sendRedirect("login.jsp?error=Invalid email or password");
            }

        } catch (SQLException e) {
            logger.error("Database error during login for email: " + email, e);
            response.sendRedirect("login.jsp?error=A technical error occurred. Please try again later.");
        } catch (Exception e) {
            logger.error("Unexpected error during login", e);
            response.sendRedirect("login.jsp?error=An unexpected error occurred");
        }
    }

    /**
     * Determines the redirect page based on user role.
     */
    private String getRedirectPage(String role) {
        if ("admin".equals(role)) {
            return "adminDashboard.jsp";
        } else if ("employer".equals(role)) {
            return "employer.jsp";
        } else {
            return "dashboard.jsp";
        }
    }

    @Override
    public void destroy() {
        logger.info("LoginServlet destroyed");
    }
}
