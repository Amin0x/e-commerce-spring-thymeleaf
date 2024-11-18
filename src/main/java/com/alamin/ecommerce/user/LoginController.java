package com.example.demo.controller;

import com.example.demo.model.LoginForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(require = false) String url, Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(
        @RequestParam(require = false) String url, 
        @Valid LoginForm loginForm, 
        BindingResult bindingResult, 
        Model model, 
        RedirectAttributes redirectAttributes 
    ) {
        if (bindingResult.hasErrors()) {
            return "login";
        }

        if (authenticationFailed) {
            model.addAttribute("errorMessage", "Invalid username or password");
            return "login";
        }
        
        return "redirect:" + (url != null ? url : "/");
    }
}
