package com.alamin.ecommerce.frontend.client.api;

import com.alamin.ecommerce.frontend.dto.CategoryDto;

import java.util.List;

/**
 * Client for the backend category catalog API ({@code /api/categories}).
 */
public interface CategoryClient {

    List<CategoryDto> list();

    List<CategoryDto> top();

    CategoryDto getById(Long id);

    CategoryDto getBySlug(String slug);

    List<CategoryDto> active(boolean active);

    List<CategoryDto> byParent(Long parentId);

    List<CategoryDto> search(String q);
}
