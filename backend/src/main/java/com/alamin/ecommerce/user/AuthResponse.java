package com.alamin.ecommerce.user;

/**
 * Backend REST authentication response: JWT plus the authenticated user's
 * identity. Mirrored in the frontend client layer (Tasks 23-28).
 */
public record AuthResponse(
    String token,
    String username,
    String role,
    String uid,
    String message
) {
}