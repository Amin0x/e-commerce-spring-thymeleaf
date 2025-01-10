package com.alamin.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {  // Renamed class for clarity

    @ExceptionHandler(CategoryNotFoundException.class)  // Handles CategoryNotFoundException
    public ResponseEntity<String> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceNotFoundException.class)  // Handles ResourceNotFoundException
    public ResponseEntity<String> handleResourceNotFoundException() {
        return new ResponseEntity<>("Resource not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)  // Handles ResourceAlreadyExistsException
    public ResponseEntity<String> handleResourceAlreadyExistsException() {
        return new ResponseEntity<>("Resource already exists", HttpStatus.CONFLICT);
    }

    // General exception handler (optional)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return new ResponseEntity<>("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
