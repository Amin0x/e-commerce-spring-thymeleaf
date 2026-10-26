package com.alamin.ecommerce.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Backend REST login request. Mirrored in the frontend client layer
 * (Tasks 23-28) as an identical DTO so both modules share the contract
 * without a shared module.
 */
@Data
public class AuthRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}
