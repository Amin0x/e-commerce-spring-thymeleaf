package com.alamin.ecommerce.frontend.user;

import com.alamin.ecommerce.frontend.client.api.AuthClient;
import com.alamin.ecommerce.frontend.dto.AuthResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Public authentication. Delegates to the backend JWT API through
 * {@link AuthClient}; on successful login/register the JWT is stored in the
 * session by {@link
 * com.alamin.ecommerce.frontend.client.api.AuthClientImpl} so that
 * {@link com.alamin.ecommerce.frontend.client.interceptor.ClientAuthInterceptor}
 * can attach it as {@code Authorization: Bearer} on subsequent calls.
 */
@Controller
@Slf4j
public class AuthController {

    private final AuthClient authClient;

    public AuthController(AuthClient authClient) {
        this.authClient = authClient;
    }

    @GetMapping("/auth/login")
    public String showLoginForm(@RequestParam(required = false) String url, Model model) {
        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute("redirectUrl", url);
        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "تسجيل الدخول");
        return "public/login";
    }

    @PostMapping("/auth/login")
    public String processLogin(@RequestParam(required = false) String url,
                               @Valid LoginForm loginForm, BindingResult bindingResult,
                               Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Invalid username or password");
            model.addAttribute("errors", bindingResult.getAllErrors());
            model.addAttribute("redirectUrl", url);
            return "public/login";
        }

        try {
            AuthResponse response = authClient.login(loginForm.getUsername(), loginForm.getPassword());
            if (response != null && response.getToken() != null) {
                if (isSafeRedirectUrl(url)) {
                    return "redirect:" + url;
                }
                return "redirect:/";
            }
            model.addAttribute("errorMessage", "Invalid username or password");
            model.addAttribute("redirectUrl", url);
            return "public/login";
        } catch (Exception e) {
            log.warn("Login failed for {}", loginForm.getUsername(), e);
            model.addAttribute("errorMessage", "Invalid username or password");
            model.addAttribute("redirectUrl", url);
            return "public/login";
        }
    }

    @GetMapping("/auth/register")
    public String showSignupForm(Model model) {
        model.addAttribute("signupForm", new SignupForm());
        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "إنشاء حساب جديد");
        return "public/signup";
    }

    @PostMapping("/auth/register")
    public String registerUser(@Valid @ModelAttribute SignupForm signupForm, BindingResult bindResult,
                               Model model, RedirectAttributes redirectAttributes) {
        log.info("registerUser: {}", signupForm.getUsername());

        if (bindResult.hasErrors()) {
            model.addAttribute("errorMessage", "Invalid input");
            model.addAttribute("errors", bindResult.getAllErrors());
            return "public/signup";
        }

        try {
            AuthResponse response = authClient.register(
                    signupForm.getUsername(), signupForm.getPassword(), signupForm.getEmail());
            if (response != null && response.getToken() != null) {
                redirectAttributes.addFlashAttribute("message", "Account created successfully");
                return "redirect:/auth/confirmation";
            }
            model.addAttribute("errorMessage", "Error creating user");
            return "public/signup";
        } catch (Exception e) {
            log.warn("Registration failed for {}", signupForm.getUsername(), e);
            model.addAttribute("errorMessage", "Error creating user");
            return "public/signup";
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

    @GetMapping("/auth/logout")
    public String logout() {
        try {
            authClient.logout();
        } catch (Exception e) {
            log.warn("Logout call failed", e);
        }
        return "redirect:/";
    }

    /**
     * Validate that a post-login redirect target is a safe local relative
     * path, preventing open redirects to external hosts.
     */
    private boolean isSafeRedirectUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        if (!url.startsWith("/")) {
            return false;
        }
        if (url.startsWith("//")) {
            return false;
        }
        return !url.contains("://");
    }
}
