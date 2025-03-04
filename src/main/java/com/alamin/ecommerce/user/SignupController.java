package com.alamin.ecommerce.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

@Controller
public class SignupController {

    @Autowired
    private UserService userService;

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("signupForm", new SignupForm());
        return "public/signup";
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> processSignup(@Valid @RequestBody SignupForm signupForm, Model model, BindingResult bindResult) {
        Map<String,Object> body = new HashMap<>();
        User user = new User();
        user.setEmail(signupForm.getEmail());
        user.setPassword(signupForm.getPassword());
        //user.setFirstName();
        try {
            userService.createUser(user);
        } catch (Exception e) {
           //bindResult.addError(new ObjectError("error", "Error creating user"));
           
           body.put("status", "failed");
           body.put("error", "Error creating user");
           
           return new ResponseEntity<Object>(body, HttpStatus.BAD_REQUEST);
        }
        
        
        body.put("status", "success");
                
        return new ResponseEntity<Object>(body, HttpStatus.OK);
    }
} 
