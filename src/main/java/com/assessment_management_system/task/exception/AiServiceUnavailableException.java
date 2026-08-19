package com.assessment_management_system.task.exception;

public class AiServiceUnavailableException extends RuntimeException {

    public AiServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public AiServiceUnavailableException(String message) {
        super(message);
    }
}