package com.alamin.ecommerce.frontend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Frontend REST registration request. Mirrored in the backend module as an
 * identical DTO so both modules share the contract without a shared module.
 */
public record RegisterRequest(
    @NotBlank(message = "Username is required") String username,
    @NotBlank(message = "Password is required") String password,
    @NotBlank(message = "Confirm password is required") String confirmPassword,
    @NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email,
    String role
) {
}
