package com.alamin.ecommerce.exception;

/**
 * Standard JSON error body returned by the backend's
 * {@link GlobalExceptionHandler @RestControllerAdvice}. Backend is a pure REST
 * module, so all errors are rendered as JSON rather than Thymeleaf view names.
 */
public class ApiError {

    private final String timestamp = java.time.Instant.now().toString();

    private final int status;

    private final String error;

    private final String message;

    private final String path;

    public ApiError(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}
