package com.alamin.ecommerce.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StateDto(
    Integer id,
    @NotBlank String name,
    String nameAr,
    @NotNull @Positive Integer countryId
) {
}