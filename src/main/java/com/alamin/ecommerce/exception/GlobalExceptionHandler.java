package com.alamin.ecommerce.exception;

import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    // Handle specific exceptions and return appropriate views
    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public String handleCategoryAlreadyExistsException(CategoryAlreadyExistsException ex) {
        return "error/404"; // Return a view name for the error page
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public String handleProductNotFoundException(ProductNotFoundException ex) {
        return "error/404"; // Return a view name for the error page
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public String handleProductAlreadyExistsException(ProductAlreadyExistsException ex) {
        return "error/404"; // Return a view name for the error page
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public String handleCategoryNotFoundException(CategoryNotFoundException ex) {
        return "error/404"; // Return a view name for the error page
    }

    @ExceptionHandler(ResourceNotFoundException.class) 
    public String handleResourceNotFoundException(ResourceNotFoundException ex) {
        return "error/404"; // Return a view name for the error page
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public String handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        return "error/404"; // Return a view name for the error page
    }
    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFoundException(NoResourceFoundException ex) {
        return "error/404"; // Return a view name for the 404 error page
    }

    @ExceptionHandler(NullPointerException.class)
    public String handleNullPointerException(NullPointerException ex) {
        return "error/500"; // Return a view name for internal server errors
    }

    @ExceptionHandler(NumberFormatException.class)
    public String handleNumberFormatException(NumberFormatException ex) {
        return "error/400"; // Return a view name for bad request errors
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return "error/404"; // Return a view name for the 404 error page
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex) {
        return "error/400"; // Return a view name for bad request errors
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException ex) {
        return "error/400"; // Return a view name for internal server errors
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return "error/400"; // Return a view name for validation errors
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return "error/405"; // Return a view name for method not allowed errors
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public String handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        return "error/415"; // Return a view name for unsupported media type errors
    }

    // General exception handler (optional)
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex) {
        return "error/400"; // Return a view name for the error page
    }

}
