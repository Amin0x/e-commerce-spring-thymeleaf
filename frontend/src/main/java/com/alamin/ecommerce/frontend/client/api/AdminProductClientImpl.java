package com.alamin.ecommerce.frontend.client.api;

import com.alamin.ecommerce.frontend.dto.ProductDto;
import com.alamin.ecommerce.frontend.dto.ProductForm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

/**
 * {@link AdminProductClient} backed by {@link RestTemplate} against the backend
 * {@code /api/products} mutation endpoints.
 */
@Component
@RequiredArgsConstructor
public class AdminProductClientImpl implements AdminProductClient {

    private final RestTemplate restTemplate;
    private final String backendBaseUrl;

    private static final String PRODUCTS_URL = "/api/products";

    @Override
    public ProductDto create(ProductForm form) {
        return restTemplate.postForObject(backendBaseUrl + PRODUCTS_URL, form, ProductDto.class);
    }

    @Override
    public ProductDto update(Long id, ProductDto product) {
        return restTemplate.exchange(
                backendBaseUrl + PRODUCTS_URL + "/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(product),
                ProductDto.class,
                id).getBody();
    }

    @Override
    public void delete(Long id) {
        restTemplate.delete(backendBaseUrl + PRODUCTS_URL + "/{id}", id);
    }

    @Override
    public void uploadImage(Long id, MultipartFile file) {
        try {
            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
            parts.add("file", new org.springframework.core.io.ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(parts, headers);
            restTemplate.postForObject(
                    backendBaseUrl + PRODUCTS_URL + "/{id}/image", request, Void.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload product image", e);
        }
    }
}
