package com.alamin.ecommerce.product;

import jakarta.validation.constraints.*;
import java.util.List;

/**
 * Request DTO for creating/updating products.
 * Contains validation annotations to ensure data integrity at the API boundary.
 */
public record ProductRequest(

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 255, message = "Product name must be between 2 and 255 characters")
    String name,

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    String description,

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be zero or positive")
    Integer price,

    @Min(value = 0, message = "Base price must be zero or positive")
    Integer basePrice,

    @Min(value = 0, message = "Price in USD must be zero or positive")
    Integer priceUSD,

    @Size(max = 100, message = "SKU cannot exceed 100 characters")
    String sku,

    @Min(value = 0, message = "Stock must be zero or positive")
    Integer stock,

    @Min(value = 0, message = "Total sold must be zero or positive")
    Integer totalSold,

    @Min(value = 0, message = "View count must be zero or positive")
    Integer viewCount,

    Boolean active,

    Boolean enabled,

    Long categoryId,

    List<Long> imageIds // For existing images to keep
) {
}