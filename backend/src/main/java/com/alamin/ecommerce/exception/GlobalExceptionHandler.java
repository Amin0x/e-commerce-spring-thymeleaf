package com.alamin.ecommerce.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Central REST error handling for the backend module. Replaces the Thymeleaf
 * view-name based handlers (and the deleted {@code MyCustomErrorController}),
 * returning a JSON {@link ApiError} body for every exception.
 *
 * <p>Security: raw exception messages are never echoed to the client for
 * unexpected failures (500 / NPE / IllegalArgument / IllegalState). Those are
 * logged server-side with a correlation ID, and the client receives a generic
 * safe message. Domain exceptions we raise ourselves (not-found / conflict /
 * validation) are safe to surface because their messages are controlled by the
 * application, not by attacker-controlled input.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static String correlationId() {
        return "corr=" + UUID.randomUUID();
    }

    /** 404 - custom resource not found (safe, app-controlled messages) */
    @ExceptionHandler({CategoryNotFoundException.class, ProductNotFoundException.class, ResourceNotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(RuntimeException ex, HttpServletRequest request) {
        String id = correlationId();
        log.warn("[{}] Resource not found on {}: {}", id, requestPath(request), ex.getMessage());
        ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), requestPath(request));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /** 409 - custom resource already exists (safe, app-controlled messages) */
    @ExceptionHandler({CategoryAlreadyExistsException.class, ProductAlreadyExistsException.class,
            ResourceAlreadyExistsException.class})
    public ResponseEntity<ApiError> handleConflict(RuntimeException ex, HttpServletRequest request) {
        String id = correlationId();
        log.warn("[{}] Resource conflict on {}: {}", id, requestPath(request), ex.getMessage());
        ApiError error = new ApiError(HttpStatus.CONFLICT.value(), "Conflict", ex.getMessage(), requestPath(request));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /** 400 - validation failures (@Valid) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String id = correlationId();
        log.warn("[{}] Validation failed on {}: {}", id, requestPath(request),
                ex.getBindingResult().getFieldErrors().stream()
                        .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                        .collect(Collectors.joining("; ")));
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), "Validation Failed", details, requestPath(request));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /** 405 - unsupported HTTP method */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                             HttpServletRequest request) {
        String id = correlationId();
        log.warn("[{}] Method not supported on {}: {}", id, requestPath(request), ex.getMessage());
        ApiError error = new ApiError(HttpStatus.METHOD_NOT_ALLOWED.value(), "Method Not Allowed",
                ex.getMessage(), requestPath(request));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    /** 415 - unsupported media type */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                                HttpServletRequest request) {
        String id = correlationId();
        log.warn("[{}] Media type not supported on {}: {}", id, requestPath(request), ex.getMessage());
        ApiError error = new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "Unsupported Media Type",
                ex.getMessage(), requestPath(request));
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(error);
    }

    /** 404 - no handler mapped for the request path */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest request) {
        String id = correlationId();
        log.warn("[{}] No handler found on {}: {}", id, requestPath(request), ex.getMessage());
        ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), requestPath(request));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /** 400 - illegal arguments (sanitized client message) */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        String id = correlationId();
        log.warn("[{}] Illegal argument on {}: {}", id, requestPath(request), ex.getMessage());
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                "Invalid request parameters", requestPath(request));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /** 400 - illegal state (sanitized client message) */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        String id = correlationId();
        log.warn("[{}] Illegal state on {}: {}", id, requestPath(request), ex.getMessage());
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                "Request cannot be completed in current state", requestPath(request));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /** 500 - null pointer (sanitized client message, logged server-side) */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiError> handleNullPointer(NullPointerException ex, HttpServletRequest request) {
        String id = correlationId();
        log.error("[{}] NullPointerException on {}: {}", id, requestPath(request), ex.getMessage(), ex);
        ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
                "An unexpected error occurred", requestPath(request));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /** 500 - any other unexpected exception (sanitized client message, logged server-side) */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        String id = correlationId();
        log.error("[{}] Unexpected error on {}: {}", id, requestPath(request), ex.getMessage(), ex);
        ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
                "An unexpected error occurred", requestPath(request));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private static String requestPath(HttpServletRequest request) {
        return request == null ? "" : request.getRequestURI();
    }
}
