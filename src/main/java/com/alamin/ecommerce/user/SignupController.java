package com.alamin.ecommerce.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;

@Controller
public class SignupController {

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("signupForm", new SignupForm());
        return "public/signup";
    }

    @PostMapping("/signup")
    public String processSignup(@Valid SignupForm signupForm) {
        // Add your form processing logic here (e.g., saving user data)
        //return "signup_success";
        return "public/login";
    }
} 
