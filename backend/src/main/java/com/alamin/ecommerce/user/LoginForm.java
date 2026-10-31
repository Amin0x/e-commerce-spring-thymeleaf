package com.alamin.ecommerce.user;

import jakarta.validation.constraints.NotBlank;

public record LoginForm(
    @NotBlank String username,
    @NotBlank String password
) {
}