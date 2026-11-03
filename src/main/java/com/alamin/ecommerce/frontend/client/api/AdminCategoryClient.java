package com.alamin.ecommerce.frontend.client.api;

import com.alamin.ecommerce.frontend.dto.CategoryDto;
import org.springframework.web.multipart.MultipartFile;

/**
 * Admin client for category management on the backend {@code /api/admin/categories}
 * endpoints.
 */
public interface AdminCategoryClient {

    CategoryDto create(String name, String description, Long parentId, MultipartFile image);

    CategoryDto update(Long id, CategoryDto category);

    CategoryDto updateImage(Long id, MultipartFile image);

    void delete(Long id);
}
