package com.alamin.ecommerce.category;

import lombok.Data;

import java.time.LocalDateTime;

public record CategoryDto(
        Long id,
        String name,
        String description,
        String  imageUrl,
        Boolean active,
        LocalDateTime created,
        LocalDateTime updated,
        Integer parent){

}
