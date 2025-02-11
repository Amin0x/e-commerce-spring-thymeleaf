package com.alamin.ecommerce.product;

import com.alamin.ecommerce.category.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public record ProductForm(
        Long id,
        String name,
        String description,
        Integer price,
        Integer initPrice,
        String sku,
        Integer stock,
        Integer totalSold,
        Boolean active,
        Boolean enabled,
        Integer category,
        List<MultipartFile> images,
        MultipartFile primaryImage
) {

}