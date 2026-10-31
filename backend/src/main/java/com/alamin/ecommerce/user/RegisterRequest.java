package com.alamin.ecommerce.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Backend REST registration request. Mirrored in the frontend client layer
 * (Tasks 23-28) as an identical DTO.
 */
public record RegisterRequest(
    @NotBlank(message = "Username is required") String username,
    @NotBlank(message = "Password is required") String password,
    @NotBlank(message = "Confirm password is required") String confirmPassword,
    @NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email,
    String role
) {
}