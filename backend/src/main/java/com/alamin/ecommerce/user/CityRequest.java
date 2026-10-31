package com.alamin.ecommerce.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CityRequest(
    Integer id,
    @NotBlank String name,
    String nameAr,
    @NotNull @Positive Integer estimatedDelivery,
    @NotBlank String estimatedDeliveryUnit,
    @NotBlank String deliveryPriority,
    @NotNull @Positive Double deliveryPrice,
    String status,
    Integer stateId,
    @NotNull @Positive Integer countryId
) {
}