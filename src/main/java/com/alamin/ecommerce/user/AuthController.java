package com.alamin.ecommerce.user;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpSession session;

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(required = false) String url, Model model) {

        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "تسجيل الدخول");
        return "public/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam(required = false) String url, @Valid LoginForm loginForm,
                               BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        System.out.println(loginForm);
        boolean authenticationFailed = false;

        if (bindingResult.hasErrors()) {
            return "public/login";
        }

        if (!authenticationFailed) {
            model.addAttribute("errorMessage", "Invalid username or password");
            return "public/login";
        }

        return "redirect:" + (url != null ? url : "/");
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("signupForm", new SignupForm());
        return "public/signup";
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignupForm signupForm, Model model, BindingResult bindResult) {
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
