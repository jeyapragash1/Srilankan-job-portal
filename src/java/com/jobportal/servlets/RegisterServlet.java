package com.jobportal.servlets;

import com.jobportal.dao.StudentDao;
import com.jobportal.dao.StudentDaoImpl;
import com.jobportal.models.Student;
import com.jobportal.utils.PasswordUtil;
import com.jobportal.utils.ValidationUtil;
import com.jobportal.utils.EmailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Enhanced RegisterServlet with BCrypt password hashing and input validation.
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(RegisterServlet.class);
    private StudentDao studentDao;

    @Override
    public void init() throws ServletException {
        studentDao = new StudentDaoImpl();
        logger.info("RegisterServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect("register.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String email = request.getParameter("email");
        String fullName = request.getParameter("fullName");
        String phoneNumber = request.getParameter("phoneNumber");
        String address = request.getParameter("address");

        try {
            // Input validation
            if (!validateInput(username, password, confirmPassword, email, fullName, phoneNumber, address)) {
                request.setAttribute("errorMessage", "All fields are required");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            // Validate email format
            if (!ValidationUtil.isValidEmail(email)) {
                request.setAttribute("errorMessage", "Invalid email format");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            // Validate phone format
            if (!ValidationUtil.isValidPhone(phoneNumber)) {
                request.setAttribute("errorMessage", "Invalid phone number format");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            // Validate username format
            if (!ValidationUtil.isValidUsername(username)) {
                request.setAttribute("errorMessage", "Username must be 3-20 characters, alphanumeric with underscores");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            // Validate password
            if (!PasswordUtil.isPasswordValid(password)) {
                request.setAttribute("errorMessage", "Password does not meet requirements: " + 
                                   PasswordUtil.getPasswordRequirements());
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                request.setAttribute("errorMessage", "Passwords do not match");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            // Sanitize inputs
            username = ValidationUtil.sanitizeForHTML(username.trim());
            email = ValidationUtil.sanitizeForHTML(email.trim());
            fullName = ValidationUtil.sanitizeForHTML(fullName.trim());
            phoneNumber = ValidationUtil.sanitizeForHTML(phoneNumber.trim());
            address = ValidationUtil.sanitizeForHTML(address.trim());

            // Hash password with BCrypt
            String hashedPassword = PasswordUtil.hashPassword(password);

            // Create Student object
            Student student = new Student();
            student.setName(fullName);
            student.setEmail(email);
            student.setPhone(phoneNumber);
            student.setAddress(address);
            student.setUsername(username);
            student.setPassword(hashedPassword);

            // Save student
            boolean isRegistered = studentDao.addStudent(student);
            
            if (isRegistered) {
                logger.info("New student registered: {} ({})", username, email);
                
                // Send welcome email
                EmailUtil.sendWelcomeEmail(email, fullName, "student");
                
                response.sendRedirect("registrationSuccess.jsp");
            } else {
                logger.warn("Registration failed for username: {}", username);
                request.setAttribute("errorMessage", "Registration failed. Please try again or use a different username/email");
                request.getRequestDispatcher("register.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            logger.error("Error during registration", e);
            request.setAttribute("errorMessage", "An error occurred during registration. Please try again");
            try {
                request.getRequestDispatcher("register.jsp").forward(request, response);
            } catch (ServletException | IOException ex) {
                logger.error("Error forwarding to register.jsp", ex);
                response.sendRedirect("register.jsp?error=Registration failed");
            }
        }
    }

    /**
     * Validates required input fields.
     */
    private boolean validateInput(String username, String password, String confirmPassword,
                                  String email, String fullName, String phoneNumber, String address) {
        return ValidationUtil.isNotBlank(username) &&
               ValidationUtil.isNotBlank(password) &&
               ValidationUtil.isNotBlank(confirmPassword) &&
               ValidationUtil.isNotBlank(email) &&
               ValidationUtil.isNotBlank(fullName) &&
               ValidationUtil.isNotBlank(phoneNumber) &&
               ValidationUtil.isNotBlank(address);
    }

    @Override
    public void destroy() {
        logger.info("RegisterServlet destroyed");
    }
}
