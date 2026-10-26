package com.alamin.ecommerce.user;

import lombok.Data;

/**
 * Backend REST authentication response: JWT plus the authenticated user's
 * identity. Mirrored in the frontend client layer (Tasks 23-28).
 */
@Data
public class AuthResponse {

    private String token;
    private String username;
    private String role;
    private String uid;
    private String message;

    public AuthResponse(String token, String username, String role, String uid, String message) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.uid = uid;
        this.message = message;
    }
}
