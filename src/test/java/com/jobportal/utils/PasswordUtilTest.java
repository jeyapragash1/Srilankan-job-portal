package com.jobportal.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PasswordUtil.
 */
class PasswordUtilTest {

    @Test
    void testHashPassword() {
        String password = "TestPassword123";
        String hashed = PasswordUtil.hashPassword(password);
        
        assertNotNull(hashed);
        assertNotEquals(password, hashed);
        assertTrue(hashed.startsWith("$2a$")); // BCrypt hash format
    }

    @Test
    void testVerifyPassword() {
        String password = "TestPassword123";
        String hashed = PasswordUtil.hashPassword(password);
        
        assertTrue(PasswordUtil.verifyPassword(password, hashed));
        assertFalse(PasswordUtil.verifyPassword("WrongPassword", hashed));
    }

    @Test
    void testIsPasswordValid() {
        assertTrue(PasswordUtil.isPasswordValid("ValidPass123"));
        assertTrue(PasswordUtil.isPasswordValid("AnotherGood1"));
        
        assertFalse(PasswordUtil.isPasswordValid("short1")); // Too short
        assertFalse(PasswordUtil.isPasswordValid("nouppercase123")); // No uppercase
        assertFalse(PasswordUtil.isPasswordValid("NOLOWERCASE123")); // No lowercase
        assertFalse(PasswordUtil.isPasswordValid("NoDigitsHere")); // No digits
        assertFalse(PasswordUtil.isPasswordValid(null)); // Null
    }

    @Test
    void testHashPasswordWithNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.hashPassword(null);
        });
    }

    @Test
    void testVerifyPasswordWithNullReturnsFalse() {
        assertFalse(PasswordUtil.verifyPassword(null, "someHash"));
        assertFalse(PasswordUtil.verifyPassword("somePassword", null));
    }
}
