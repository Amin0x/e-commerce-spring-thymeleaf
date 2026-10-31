package com.alamin.ecommerce.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record UserRequest(
    @NotBlank String role,
    @NotBlank @Email String email,
    String avatar,
    Boolean enabled,
    String status,
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotNull Integer day,
    @NotNull Integer month,
    @NotNull Integer year,
    MultipartFile image,
    String address,
    String city,
    String phone,
    String state,
    String country
) {
}