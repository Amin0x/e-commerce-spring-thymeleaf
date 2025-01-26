package com.alamin.ecommerce.product;

import com.alamin.ecommerce.category.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ProductForm(
        Long id,
        String name,
        String description,
        Integer price,
        Integer initPrice,
        String sku,
        Integer stock,
        int totalSold,
        Boolean active,
        LocalDateTime created,
        LocalDateTime updated,
        LocalDateTime deleted,
        Integer category
) {}