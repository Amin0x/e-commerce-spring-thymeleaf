package com.alamin.ecommerce.frontend.client.api;

import com.alamin.ecommerce.frontend.dto.ProductDto;
import com.alamin.ecommerce.frontend.dto.ReviewDto;

import java.util.List;

/**
 * Client for the backend product catalog API ({@code /api/products}).
 */
public interface ProductClient {

    List<ProductDto> list(int page, int size, String sortBy, boolean asc);

    ProductDto getById(Long id);

    ProductDto getByUuid(String uuid);

    ProductDto getBySlug(String slug);

    List<ProductDto> random(int size);

    List<ProductDto> bestSelling(int size);

    List<ProductDto> newArrivals(int size);

    List<ProductDto> byCategory(Long categoryId, int size);

    List<ReviewDto> getReviews(Long productId);
}
