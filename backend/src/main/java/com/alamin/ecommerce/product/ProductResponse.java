package com.alamin.ecommerce.product;

import java.util.List;

public record ProductResponse (
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
        List<String> images,
        String image,
        String discount,
        boolean isNew
) {
}
