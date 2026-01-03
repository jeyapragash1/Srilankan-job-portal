package com.jobportal.exception;

/**
 * Exception thrown when database operations fail.
 */
public class DatabaseException extends JobPortalException {
    
    private static final long serialVersionUID = 1L;

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseException(Throwable cause) {
        super(cause);
    }
}
