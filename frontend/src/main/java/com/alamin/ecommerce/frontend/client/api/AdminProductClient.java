package com.alamin.ecommerce.frontend.client.api;

import com.alamin.ecommerce.frontend.dto.ProductDto;
import com.alamin.ecommerce.frontend.dto.ProductForm;
import org.springframework.web.multipart.MultipartFile;

/**
 * Admin client for product mutation on the backend {@code /api/products}
 * endpoints (create, update, delete, image upload).
 */
public interface AdminProductClient {

    ProductDto create(ProductForm form);

    ProductDto update(Long id, ProductDto product);

    void delete(Long id);

    void uploadImage(Long id, MultipartFile file);
}
