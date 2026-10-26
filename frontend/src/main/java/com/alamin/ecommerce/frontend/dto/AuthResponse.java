package com.alamin.ecommerce.frontend.dto;

/**
 * Mirrored copy of the backend {@link com.alamin.ecommerce.user.AuthResponse}
 * returned by {@code POST /api/auth/login} and {@code POST /api/auth/register}.
 */
public class AuthResponse {

    private String token;
    private String username;
    private String role;
    private String uid;
    private String message;

    public AuthResponse() {
    }

    public AuthResponse(String token, String username, String role, String uid, String message) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.uid = uid;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
