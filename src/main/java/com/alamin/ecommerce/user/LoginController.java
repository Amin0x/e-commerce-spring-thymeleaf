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
    public String processLogin(@RequestParam(require = false) String url, LoginForm loginForm) {
        // Add your form processing logic here (e.g., authentication)
        return "redirect:home";
    }
}
