package com.alamin.ecommerce.order;

public record OrderDto(
        String paymentMethod,
        String cardNumber,
        String ccv,
        String cardDate,
        String cardHolder,
        String carrier,
        String street,
        String city,
        String state,
        String country,
        String postalCode,
        String firstName,
        String lastName
) {

}
