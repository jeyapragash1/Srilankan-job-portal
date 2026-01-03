package com.jobportal.exception;

/**
 * Base exception for all custom exceptions in the job portal application.
 */
public class JobPortalException extends Exception {
    
    private static final long serialVersionUID = 1L;

    public JobPortalException(String message) {
        super(message);
    }

    public JobPortalException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobPortalException(Throwable cause) {
        super(cause);
    }
}
