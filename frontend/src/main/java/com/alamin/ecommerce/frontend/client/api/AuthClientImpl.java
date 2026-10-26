package com.alamin.ecommerce.frontend.client.api;

import com.alamin.ecommerce.frontend.dto.AuthResponse;
import com.alamin.ecommerce.frontend.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;

/**
 * {@link AuthClient} backed by a {@link RestTemplate} against the backend
 * {@code /api/auth} endpoints.
 *
 * <p>On successful login/register the returned JWT is stored in the HTTP session
 * so that {@link com.alamin.ecommerce.frontend.client.interceptor.ClientAuthInterceptor}
 * can attach it to subsequent outbound calls as {@code Authorization: Bearer}.
 */
@Component
@RequiredArgsConstructor
public class AuthClientImpl implements AuthClient {

    private final RestTemplate restTemplate;
    private final String backendBaseUrl;

    @Override
    public AuthResponse login(String username, String password) {
        AuthRequestBody body = new AuthRequestBody(username, password);
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                backendBaseUrl + "/api/auth/login", body, AuthResponse.class);
        AuthResponse authResponse = response.getBody();
        if (authResponse != null && authResponse.getToken() != null) {
            storeTokenInSession(authResponse.getToken());
        }
        return authResponse;
    }

    @Override
    public AuthResponse register(String username, String password, String email) {
        RegisterRequest body = new RegisterRequest(username, password, email);
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                backendBaseUrl + "/api/auth/register", body, AuthResponse.class);
        AuthResponse authResponse = response.getBody();
        if (authResponse != null && authResponse.getToken() != null) {
            storeTokenInSession(authResponse.getToken());
        }
        return authResponse;
    }

    @Override
    public AuthResponse me() {
        return restTemplate.getForObject(backendBaseUrl + "/api/auth/me", AuthResponse.class);
    }

    @Override
    public void logout() {
        clearTokenFromSession();
        restTemplate.postForObject(backendBaseUrl + "/api/auth/logout", null, AuthResponse.class);
    }

    private void storeTokenInSession(String token) {
        HttpSession session = currentSession(true);
        if (session != null) {
            session.setAttribute("jwtToken", token);
        }
    }

    private void clearTokenFromSession() {
        HttpSession session = currentSession(false);
        if (session != null) {
            session.removeAttribute("jwtToken");
        }
    }

    @SuppressWarnings("unchecked")
    private HttpSession currentSession(boolean create) {
        try {
            var attrs = RequestContextHolder.getRequestAttributes();
            if (attrs == null || !(attrs instanceof ServletRequestAttributes)) {
                return null;
            }
            return ((ServletRequestAttributes) attrs).getRequest().getSession(create);
        } catch (IllegalStateException e) {
            return null;
        }
    }

    /** Minimal login request body (mirrors backend {@code AuthRequest}). */
    private static class AuthRequestBody {
        private final String username;
        private final String password;

        AuthRequestBody(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
