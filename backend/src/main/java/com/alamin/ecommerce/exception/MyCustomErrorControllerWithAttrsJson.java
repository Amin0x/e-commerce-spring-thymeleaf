package com.alamin.ecommerce.exception;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class MyCustomErrorControllerWithAttrsJson implements ErrorController {

    @Autowired
    private ErrorAttributes errorAttributes;

    @RequestMapping("/error")
    public @ResponseBody Map<String, Object> handleProblem(HttpServletRequest req){

        ServletWebRequest webRequest = new ServletWebRequest(req);
        ErrorAttributeOptions options = ErrorAttributeOptions.defaults()
                .including(ErrorAttributeOptions.Include.MESSAGE)
                .including(ErrorAttributeOptions.Include.BINDING_ERRORS)
                .including(ErrorAttributeOptions.Include.EXCEPTION)
                .including(ErrorAttributeOptions.Include.STACK_TRACE);
        // .including(ErrorAttributeOptions.Include.REQUEST_URI)
        Map<String, Object> errors = errorAttributes.getErrorAttributes(webRequest, options);

        return errors;

    }

    

}
