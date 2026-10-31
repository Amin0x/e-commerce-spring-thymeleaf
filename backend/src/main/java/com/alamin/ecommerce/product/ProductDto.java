package com.alamin.ecommerce.product;

import java.util.List;

public record ProductDto(

    Long productId,
    String uuid,
    String name,
    String description,
    Integer price,
    Integer basePrice,
    Integer priceUSD,
    String sku,
    String slug,
    Integer stock,
    Integer totalSold,
    Integer viewCount,
    Boolean active,
    Boolean enabled,
    String image,
    List<ProductImage> productImages,
    Long categoryId,
    String categoryName,
    Boolean isNewArrive,
    Double percent
) {

    public static ProductDto from(Product product) {
        if (product == null) return null;
        return new ProductDto(
            product.getId(),
            product.getUuid(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getBasePrice(),
            product.getPriceUSD(),
            product.getSku(),
            product.getSlug(),
            product.getStock(),
            product.getTotalSold(),
            product.getViewCount(),
            product.getActive(),
            product.getEnabled(),
            product.getImage(),
            product.getProductImages(),
            product.getCategory() != null ? product.getCategory().getId() : null,
            product.getCategory() != null ? product.getCategory().getName() : null,
            null, // isNewArrive - not on Product entity
            null  // percent - not on Product entity
        );
    }

    public static ProductDto of(
            String name,
            Long productId,
            String uuid,
            String description,
            Integer price,
            Integer basePrice,
            Integer priceUSD,
            String sku,
            Integer stock,
            Integer totalSold,
            Integer viewCount,
            Boolean active,
            Boolean enabled,
            String image,
            List<ProductImage> productImages,
            Long categoryId,
            String categoryName,
            Boolean isNewArrive,
            Double percent) {
        return new ProductDto(
            productId, uuid, name, description, price, basePrice, priceUSD, sku, null,
            stock, totalSold, viewCount, active, enabled, image, productImages, categoryId,
            categoryName, isNewArrive, percent
        );
    }
}