package com.alamin.ecommerce.frontend.client.api;

import com.alamin.ecommerce.frontend.dto.CategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

/**
 * {@link AdminCategoryClient} backed by {@link RestTemplate} against the backend
 * {@code /api/admin/categories} endpoints.
 */
@Component
@RequiredArgsConstructor
public class AdminCategoryClientImpl implements AdminCategoryClient {

    private final RestTemplate restTemplate;
    private final String backendBaseUrl;

    private static final String CATEGORIES_URL = "/api/admin/categories";

    @Override
    public CategoryDto create(String name, String description, Long parentId, MultipartFile image) {
        try {
            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
            parts.add("name", name);
            if (description != null) {
                parts.add("description", description);
            }
            if (parentId != null) {
                parts.add("parentId", parentId);
            }
            if (image != null && !image.isEmpty()) {
                parts.add("image", new org.springframework.core.io.ByteArrayResource(image.getBytes()) {
                    @Override
                    public String getFilename() {
                        return image.getOriginalFilename();
                    }
                });
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            return restTemplate.postForObject(
                    backendBaseUrl + CATEGORIES_URL, new HttpEntity<>(parts, headers), CategoryDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create category", e);
        }
    }

    @Override
    public CategoryDto update(Long id, CategoryDto category) {
        return restTemplate.exchange(
                backendBaseUrl + CATEGORIES_URL + "/{id}",
                org.springframework.http.HttpMethod.PUT,
                new HttpEntity<>(category),
                CategoryDto.class,
                id).getBody();
    }

    @Override
    public CategoryDto updateImage(Long id, MultipartFile image) {
        try {
            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
            parts.add("image", new org.springframework.core.io.ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            });
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            return restTemplate.exchange(
                    backendBaseUrl + CATEGORIES_URL + "/{id}/image",
                    org.springframework.http.HttpMethod.PUT,
                    new HttpEntity<>(parts, headers),
                    CategoryDto.class,
                    id).getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update category image", e);
        }
    }

    @Override
    public void delete(Long id) {
        restTemplate.delete(backendBaseUrl + CATEGORIES_URL + "/{id}", id);
    }
}
