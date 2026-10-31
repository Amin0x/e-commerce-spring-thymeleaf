package com.alamin.ecommerce.user;

import jakarta.validation.constraints.NotBlank;

public record SignupForm(
    @NotBlank(message = "Username is required") String username,
    @NotBlank(message = "Password is required") String password,
    @NotBlank(message = "Confirm password is required") String confirmPassword
) {
}