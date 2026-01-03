package com.jobportal.utils;

import org.apache.commons.lang3.StringUtils;
import org.owasp.encoder.Encode;

import java.util.regex.Pattern;

/**
 * Input validation and sanitization utilities.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[0-9]{10,15}$"
    );

    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9 ]+$"
    );

    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_]{3,20}$"
    );

    /**
     * Validates email format.
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates phone number format.
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.replaceAll("[\\s-]", "")).matches();
    }

    /**
     * Validates username format.
     */
    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * Validates if string contains only alphanumeric characters.
     */
    public static boolean isAlphanumeric(String input) {
        return input != null && ALPHANUMERIC_PATTERN.matcher(input).matches();
    }

    /**
     * Sanitizes input to prevent XSS attacks.
     */
    public static String sanitizeForHTML(String input) {
        if (input == null) {
            return null;
        }
        return Encode.forHtml(input);
    }

    /**
     * Sanitizes input for HTML attributes.
     */
    public static String sanitizeForHTMLAttribute(String input) {
        if (input == null) {
            return null;
        }
        return Encode.forHtmlAttribute(input);
    }

    /**
     * Sanitizes input for JavaScript context.
     */
    public static String sanitizeForJavaScript(String input) {
        if (input == null) {
            return null;
        }
        return Encode.forJavaScript(input);
    }

    /**
     * Validates string is not null, empty, or only whitespace.
     */
    public static boolean isNotBlank(String input) {
        return StringUtils.isNotBlank(input);
    }

    /**
     * Validates string length is within bounds.
     */
    public static boolean isLengthValid(String input, int min, int max) {
        if (input == null) {
            return false;
        }
        int length = input.length();
        return length >= min && length <= max;
    }

    /**
     * Validates integer is within range.
     */
    public static boolean isIntInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * Validates file extension is allowed.
     */
    public static boolean isValidFileExtension(String filename, String[] allowedExtensions) {
        if (filename == null || allowedExtensions == null) {
            return false;
        }
        
        String extension = getFileExtension(filename);
        if (extension == null) {
            return false;
        }

        for (String allowed : allowedExtensions) {
            if (extension.equalsIgnoreCase(allowed)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extracts file extension from filename.
     */
    public static String getFileExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0 && lastDot < filename.length() - 1) {
            return filename.substring(lastDot + 1);
        }
        return null;
    }

    /**
     * Validates role is valid.
     */
    public static boolean isValidRole(String role) {
        return role != null && 
               (role.equals("student") || role.equals("employer") || role.equals("admin"));
    }

    /**
     * Validates application status is valid.
     */
    public static boolean isValidApplicationStatus(String status) {
        return status != null && 
               (status.equals("applied") || status.equals("interviewed") || 
                status.equals("accepted") || status.equals("rejected"));
    }
}
