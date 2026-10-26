package com.alamin.ecommerce.frontend.client.api;

import com.alamin.ecommerce.frontend.dto.AuthResponse;
import com.alamin.ecommerce.frontend.dto.RegisterRequest;

/**
 * Client for the backend authentication API ({@code /api/auth}).
 */
public interface AuthClient {

    AuthResponse login(String username, String password);

    AuthResponse register(String username, String password, String email);

    AuthResponse me();

    void logout();
}
