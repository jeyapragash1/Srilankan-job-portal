package com.jobportal.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationUtil.
 */
class ValidationUtilTest {

    @Test
    void testIsValidEmail() {
        assertTrue(ValidationUtil.isValidEmail("test@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user.name@example.co.uk"));
        assertTrue(ValidationUtil.isValidEmail("user+tag@domain.com"));
        
        assertFalse(ValidationUtil.isValidEmail("invalid.email"));
        assertFalse(ValidationUtil.isValidEmail("@example.com"));
        assertFalse(ValidationUtil.isValidEmail("test@"));
        assertFalse(ValidationUtil.isValidEmail(null));
    }

    @Test
    void testIsValidPhone() {
        assertTrue(ValidationUtil.isValidPhone("1234567890"));
        assertTrue(ValidationUtil.isValidPhone("12345678901234"));
        assertTrue(ValidationUtil.isValidPhone("123-456-7890"));
        assertTrue(ValidationUtil.isValidPhone("123 456 7890"));
        
        assertFalse(ValidationUtil.isValidPhone("123")); // Too short
        assertFalse(ValidationUtil.isValidPhone("12345678901234567890")); // Too long
        assertFalse(ValidationUtil.isValidPhone("abcd123456"));
        assertFalse(ValidationUtil.isValidPhone(null));
    }

    @Test
    void testIsValidUsername() {
        assertTrue(ValidationUtil.isValidUsername("user123"));
        assertTrue(ValidationUtil.isValidUsername("test_user"));
        assertTrue(ValidationUtil.isValidUsername("User_Name_123"));
        
        assertFalse(ValidationUtil.isValidUsername("ab")); // Too short
        assertFalse(ValidationUtil.isValidUsername("this_is_a_very_long_username")); // Too long
        assertFalse(ValidationUtil.isValidUsername("user-name")); // Invalid character
        assertFalse(ValidationUtil.isValidUsername("user name")); // Space
        assertFalse(ValidationUtil.isValidUsername(null));
    }

    @Test
    void testIsNotBlank() {
        assertTrue(ValidationUtil.isNotBlank("test"));
        assertTrue(ValidationUtil.isNotBlank(" test "));
        
        assertFalse(ValidationUtil.isNotBlank(""));
        assertFalse(ValidationUtil.isNotBlank("   "));
        assertFalse(ValidationUtil.isNotBlank(null));
    }

    @Test
    void testIsLengthValid() {
        assertTrue(ValidationUtil.isLengthValid("test", 1, 10));
        assertTrue(ValidationUtil.isLengthValid("hello", 5, 5));
        
        assertFalse(ValidationUtil.isLengthValid("test", 5, 10));
        assertFalse(ValidationUtil.isLengthValid("testing", 1, 5));
        assertFalse(ValidationUtil.isLengthValid(null, 1, 10));
    }

    @Test
    void testIsValidFileExtension() {
        String[] allowed = {"pdf", "doc", "docx"};
        
        assertTrue(ValidationUtil.isValidFileExtension("resume.pdf", allowed));
        assertTrue(ValidationUtil.isValidFileExtension("document.DOC", allowed));
        assertTrue(ValidationUtil.isValidFileExtension("file.docx", allowed));
        
        assertFalse(ValidationUtil.isValidFileExtension("image.jpg", allowed));
        assertFalse(ValidationUtil.isValidFileExtension("noext", allowed));
        assertFalse(ValidationUtil.isValidFileExtension(null, allowed));
    }

    @Test
    void testIsValidRole() {
        assertTrue(ValidationUtil.isValidRole("student"));
        assertTrue(ValidationUtil.isValidRole("employer"));
        assertTrue(ValidationUtil.isValidRole("admin"));
        
        assertFalse(ValidationUtil.isValidRole("invalid"));
        assertFalse(ValidationUtil.isValidRole("Student")); // Case sensitive
        assertFalse(ValidationUtil.isValidRole(null));
    }

    @Test
    void testIsValidApplicationStatus() {
        assertTrue(ValidationUtil.isValidApplicationStatus("applied"));
        assertTrue(ValidationUtil.isValidApplicationStatus("interviewed"));
        assertTrue(ValidationUtil.isValidApplicationStatus("accepted"));
        assertTrue(ValidationUtil.isValidApplicationStatus("rejected"));
        
        assertFalse(ValidationUtil.isValidApplicationStatus("pending"));
        assertFalse(ValidationUtil.isValidApplicationStatus(null));
    }

    @Test
    void testSanitizeForHTML() {
        assertEquals("&lt;script&gt;alert(&#39;xss&#39;)&lt;/script&gt;", 
                    ValidationUtil.sanitizeForHTML("<script>alert('xss')</script>"));
        assertEquals("Test &amp; text", 
                    ValidationUtil.sanitizeForHTML("Test & text"));
        assertNull(ValidationUtil.sanitizeForHTML(null));
    }
}
