package com.alamin.ecommerce.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Backend REST registration request. Mirrored in the frontend client layer
 * (Tasks 23-28) as an identical DTO.
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    private String role; // optional; defaults to USER
}
