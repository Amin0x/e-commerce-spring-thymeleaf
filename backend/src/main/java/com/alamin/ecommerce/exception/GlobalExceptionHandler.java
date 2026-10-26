package com.alamin.ecommerce.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * Central REST error handling for the backend module. Replaces the Thymeleaf
 * view-name based handlers (and the deleted {@code MyCustomErrorController}),
 * returning a JSON {@link ApiError} body for every exception.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 404 - custom resource not found */
    @ExceptionHandler({CategoryNotFoundException.class, ProductNotFoundException.class, ResourceNotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(RuntimeException ex, HttpServletRequest request) {
        ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), requestPath(request));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /** 409 - custom resource already exists */
    @ExceptionHandler({CategoryAlreadyExistsException.class, ProductAlreadyExistsException.class, ResourceAlreadyExistsException.class})
    public ResponseEntity<ApiError> handleConflict(RuntimeException ex, HttpServletRequest request) {
        ApiError error = new ApiError(HttpStatus.CONFLICT.value(), "Conflict", ex.getMessage(), requestPath(request));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /** 400 - validation failures (@Valid) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
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
        ApiError error = new ApiError(HttpStatus.METHOD_NOT_ALLOWED.value(), "Method Not Allowed",
                ex.getMessage(), requestPath(request));
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    /** 415 - unsupported media type */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                                HttpServletRequest request) {
        ApiError error = new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "Unsupported Media Type",
                ex.getMessage(), requestPath(request));
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(error);
    }

    /** 404 - no handler mapped for the request path */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest request) {
        ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), requestPath(request));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /** 400 - illegal arguments */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(), requestPath(request));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /** 400 - illegal state */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(), requestPath(request));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /** 500 - null pointer */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiError> handleNullPointer(NullPointerException ex, HttpServletRequest request) {
        ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", ex.getMessage(),
                requestPath(request));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /** 500 - any other unexpected exception */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", ex.getMessage(),
                requestPath(request));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private static String requestPath(HttpServletRequest request) {
        return request == null ? "" : request.getRequestURI();
    }
}
