package com.alamin.ecommerce.user;

import jakarta.validation.constraints.NotBlank;

/**
 * Backend REST login request. Mirrored in the frontend client layer
 * (Tasks 23-28) as an identical DTO so both modules share the contract
 * without a shared module.
 */
public record AuthRequest(
    @NotBlank(message = "Username is required") String username,
    @NotBlank(message = "Password is required") String password
) {
}