package com.alamin.ecommerce.frontend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Frontend REST login request. Mirrored in the backend module as an identical
 * DTO so both modules share the contract without a shared module.
 */
public record AuthRequest(
    @NotBlank(message = "Username is required") String username,
    @NotBlank(message = "Password is required") String password
) {
}
