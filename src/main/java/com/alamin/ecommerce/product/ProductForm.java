package com.alamin.ecommerce.product;

import org.springframework.web.multipart.MultipartFile;

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