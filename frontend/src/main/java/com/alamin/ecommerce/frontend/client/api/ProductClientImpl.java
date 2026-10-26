package com.alamin.ecommerce.frontend.client.api;

import com.alamin.ecommerce.frontend.dto.ProductDto;
import com.alamin.ecommerce.frontend.dto.ReviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * {@link ProductClient} backed by {@link RestTemplate} against the backend
 * {@code /api/products} endpoints.
 */
@Component
@RequiredArgsConstructor
public class ProductClientImpl implements ProductClient {

    private final RestTemplate restTemplate;
    private final String backendBaseUrl;

    private static final ParameterizedTypeReference<List<ProductDto>> PRODUCT_LIST =
            new ParameterizedTypeReference<>() {
            };

    @Override
    public List<ProductDto> list(int page, int size, String sortBy, boolean asc) {
        var uri = UriComponentsBuilder.fromHttpUrl(backendBaseUrl + "/api/products")
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sortBy", sortBy)
                .queryParam("asc", asc)
                .build().toUri();
        return restTemplate.exchange(uri, org.springframework.http.HttpMethod.GET, null,
                PRODUCT_LIST).getBody();
    }

    @Override
    public ProductDto getById(Long id) {
        return restTemplate.getForObject(backendBaseUrl + "/api/products/{id}", ProductDto.class, id);
    }

    @Override
    public ProductDto getByUuid(String uuid) {
        return restTemplate.getForObject(backendBaseUrl + "/api/products/by-uuid/{uuid}",
                ProductDto.class, uuid);
    }

    @Override
    public List<ProductDto> random(int size) {
        return restTemplate.exchange(
                backendBaseUrl + "/api/products/random?size={size}",
                org.springframework.http.HttpMethod.GET, null, PRODUCT_LIST, size).getBody();
    }

    @Override
    public List<ProductDto> bestSelling(int size) {
        return restTemplate.exchange(
                backendBaseUrl + "/api/products/best-selling?size={size}",
                org.springframework.http.HttpMethod.GET, null, PRODUCT_LIST, size).getBody();
    }

    @Override
    public List<ProductDto> newArrivals(int size) {
        return restTemplate.exchange(
                backendBaseUrl + "/api/products/new-arrivals?size={size}",
                org.springframework.http.HttpMethod.GET, null, PRODUCT_LIST, size).getBody();
    }

    @Override
    public List<ProductDto> byCategory(Long categoryId, int size) {
        return restTemplate.exchange(
                backendBaseUrl + "/api/products/category/{categoryId}?size={size}",
                org.springframework.http.HttpMethod.GET, null, PRODUCT_LIST, categoryId, size)
                .getBody();
    }

    private static final ParameterizedTypeReference<List<ReviewDto>> REVIEW_LIST =
            new ParameterizedTypeReference<>() {
            };

    @Override
    public List<ReviewDto> getReviews(Long productId) {
        return restTemplate.exchange(
                backendBaseUrl + "/api/products/{productId}/reviews",
                org.springframework.http.HttpMethod.GET, null, REVIEW_LIST, productId).getBody();
    }
}
