package com.jobportal.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for password hashing and verification using BCrypt.
 */
public class PasswordUtil {
    
    private static final int BCRYPT_ROUNDS = 12;

    /**
     * Hashes a plain text password using BCrypt.
     *
     * @param plainTextPassword the password to hash
     * @return the hashed password
     */
    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    /**
     * Verifies a plain text password against a hashed password.
     *
     * @param plainTextPassword the plain text password
     * @param hashedPassword the hashed password to check against
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Checks if a password meets security requirements.
     *
     * @param password the password to validate
     * @return true if password meets requirements, false otherwise
     */
    public static boolean isPasswordValid(String password) {
        if (password == null) {
            return false;
        }

        int minLength = ConfigManager.getIntProperty("security.password.minLength", 8);
        boolean requireUppercase = ConfigManager.getBooleanProperty("security.password.requireUppercase", true);
        boolean requireLowercase = ConfigManager.getBooleanProperty("security.password.requireLowercase", true);
        boolean requireDigit = ConfigManager.getBooleanProperty("security.password.requireDigit", true);
        boolean requireSpecialChar = ConfigManager.getBooleanProperty("security.password.requireSpecialChar", false);

        if (password.length() < minLength) {
            return false;
        }

        if (requireUppercase && !password.matches(".*[A-Z].*")) {
            return false;
        }

        if (requireLowercase && !password.matches(".*[a-z].*")) {
            return false;
        }

        if (requireDigit && !password.matches(".*\\d.*")) {
            return false;
        }

        if (requireSpecialChar && !password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return false;
        }

        return true;
    }

    /**
     * Gets password validation error message.
     *
     * @return descriptive error message
     */
    public static String getPasswordRequirements() {
        int minLength = ConfigManager.getIntProperty("security.password.minLength", 8);
        StringBuilder requirements = new StringBuilder("Password must be at least " + minLength + " characters");
        
        if (ConfigManager.getBooleanProperty("security.password.requireUppercase", true)) {
            requirements.append(", contain uppercase letters");
        }
        if (ConfigManager.getBooleanProperty("security.password.requireLowercase", true)) {
            requirements.append(", lowercase letters");
        }
        if (ConfigManager.getBooleanProperty("security.password.requireDigit", true)) {
            requirements.append(", numbers");
        }
        if (ConfigManager.getBooleanProperty("security.password.requireSpecialChar", false)) {
            requirements.append(", special characters");
        }
        
        return requirements.toString();
    }
}
