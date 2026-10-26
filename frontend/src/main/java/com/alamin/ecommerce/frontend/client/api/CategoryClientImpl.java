package com.alamin.ecommerce.frontend.client.api;

import com.alamin.ecommerce.frontend.dto.CategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * {@link CategoryClient} backed by {@link RestTemplate} against the backend
 * {@code /api/categories} endpoints.
 */
@Component
@RequiredArgsConstructor
public class CategoryClientImpl implements CategoryClient {

    private final RestTemplate restTemplate;
    private final String backendBaseUrl;

    private static final ParameterizedTypeReference<List<CategoryDto>> CATEGORY_LIST =
            new ParameterizedTypeReference<>() {
            };

    @Override
    public List<CategoryDto> list() {
        return restTemplate.exchange(backendBaseUrl + "/api/categories",
                org.springframework.http.HttpMethod.GET, null, CATEGORY_LIST).getBody();
    }

    @Override
    public List<CategoryDto> top() {
        return restTemplate.exchange(backendBaseUrl + "/api/categories/top",
                org.springframework.http.HttpMethod.GET, null, CATEGORY_LIST).getBody();
    }

    @Override
    public CategoryDto getById(Long id) {
        return restTemplate.getForObject(backendBaseUrl + "/api/categories/{id}",
                CategoryDto.class, id);
    }

    @Override
    public CategoryDto getBySlug(String slug) {
        return restTemplate.getForObject(backendBaseUrl + "/api/categories/slug/{slug}",
                CategoryDto.class, slug);
    }

    @Override
    public List<CategoryDto> active(boolean active) {
        var uri = UriComponentsBuilder.fromHttpUrl(backendBaseUrl + "/api/categories/active")
                .queryParam("active", active).build().toUri();
        return restTemplate.exchange(uri, org.springframework.http.HttpMethod.GET, null,
                CATEGORY_LIST).getBody();
    }

    @Override
    public List<CategoryDto> byParent(Long parentId) {
        return restTemplate.exchange(backendBaseUrl + "/api/categories/parent/{parentId}",
                org.springframework.http.HttpMethod.GET, null, CATEGORY_LIST, parentId).getBody();
    }

    @Override
    public List<CategoryDto> search(String q) {
        var uri = UriComponentsBuilder.fromHttpUrl(backendBaseUrl + "/api/categories/search")
                .queryParam("q", q).build().toUri();
        return restTemplate.exchange(uri, org.springframework.http.HttpMethod.GET, null,
                CATEGORY_LIST).getBody();
    }
}
