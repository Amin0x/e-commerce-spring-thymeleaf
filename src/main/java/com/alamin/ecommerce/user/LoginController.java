package com.alamin.ecommerce.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.BindingResult;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private HttpSession session;

    @GetMapping("/login")
    public String showLoginForm(
        @RequestParam(required = false) String url, 
        Model model
    ) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(
        @RequestParam(required = false) String url, 
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
