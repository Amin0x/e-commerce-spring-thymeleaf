package com.alamin.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {  // Renamed class for clarity

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<String> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

//    @ExceptionHandler(CategoryNotFoundException.class)
//    public String handleCategoryNotFoundException(CategoryNotFoundException ex, Model model) {
//        model.addAttribute("message", ex.getMessage());
//        return "error/404";
//    }

    @ExceptionHandler(ResourceNotFoundException.class) 
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>("Resource not found: " + ex.getMessage(), HttpStatus.NOT_FOUND);
    }

//    @ExceptionHandler(ResourceNotFoundException.class)
//    public String handleResourceNotFoundException(ResourceNotFoundException ex, Model model) {
//        return "error/404";
//    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<String> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        return new ResponseEntity<>("Resource already exists", HttpStatus.CONFLICT);
    }

//    @ExceptionHandler(ResourceAlreadyExistsException.class)
//    public String handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex, Model model) {
//        return "error/404";
//    }

    // General exception handler (optional)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return new ResponseEntity<>("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

//    // General exception handler (optional)
//    @ExceptionHandler(Exception.class)
//    public String handleGenericException(Exception ex, Model model) {
//        return "error/500";
//    }


}
