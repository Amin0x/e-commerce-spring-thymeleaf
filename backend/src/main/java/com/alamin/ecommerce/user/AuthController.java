package com.alamin.ecommerce.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService , AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @GetMapping("/auth/login")
    public String showLoginForm(@RequestParam(required = false) String url, Model model) {

        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "تسجيل الدخول");
        return "public/login";
    }

    @PostMapping("/auth/login")
    public String processLogin(@RequestParam(required = false) String url, @Valid LoginForm loginForm,
                               BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        System.out.println("processLogin: " + loginForm);
        System.out.println("----------------------------------------");

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Invalid username or password");
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "public/login";
        }

        try {
            authenticateUser(loginForm.getUsername(), loginForm.getPassword());
            if (StringUtils.hasText(url)) {
                return "redirect:" + url;
            } else {
                return "redirect:/";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Invalid username or password");
            return "public/login";
        }
        
    }

    @GetMapping("/auth/register")
    public String showSignupForm(Model model) {
        model.addAttribute("signupForm", new SignupForm());
        return "public/signup";
    }

    @PostMapping("/auth/register")
    public ResponseEntity<Map<String,Object>> registerUser(@Valid @RequestBody SignupForm signupForm, BindingResult bindResult) {
        log.info("registerUser: {}", signupForm);
        Map<String,Object> body = new HashMap<>();
       
        if (bindResult.hasErrors()) {
            //bindResult.addError(new ObjectError("error", "Invalid input"));

            body.put("status", "failed");
            body.put("message", "Invalid input");
            body.put("errors", bindResult.getAllErrors());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        try {
            userService.createUser(signupForm);
            body.put("status", "success");
            body.put("message", "User created successfully");
            body.put("url", "/auth/confirmation");
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (Exception e) {

           body.put("status", "failed");
           body.put("message", "Error creating user");
           return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        
    }

    @GetMapping("/auth/confirmation")
    public String showConfirmationPage(Model model) {

        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "تأكيد الحساب");

        return "public/confirmEmail";
    }

    public void authenticateUser(String username, String password) {
        UsernamePasswordAuthenticationToken unauthenticated = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        Authentication authenticate = authenticationManager.authenticate(unauthenticated);
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        log.info("User authenticated successfully: {}", username);
    }
} 
