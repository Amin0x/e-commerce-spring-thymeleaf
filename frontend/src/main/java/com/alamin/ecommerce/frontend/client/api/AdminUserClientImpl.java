package com.alamin.ecommerce.frontend.client.api;

import com.alamin.ecommerce.frontend.dto.PageDto;
import com.alamin.ecommerce.frontend.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * {@link AdminUserClient} backed by {@link RestTemplate} against the backend
 * {@code /api/admin/users} endpoints.
 */
@Component
@RequiredArgsConstructor
public class AdminUserClientImpl implements AdminUserClient {

    private static final ParameterizedTypeReference<PageDto<UserDto>> USER_PAGE =
            new ParameterizedTypeReference<>() {
            };

    private final RestTemplate restTemplate;
    private final String backendBaseUrl;

    private static final String USERS_URL = "/api/admin/users";

    @Override
    public PageDto<UserDto> list(int page, int size) {
        var uri = UriComponentsBuilder.fromUriString(backendBaseUrl + USERS_URL)
                .queryParam("page", page)
                .queryParam("size", size)
                .build().toUri();
        return restTemplate.exchange(uri, HttpMethod.GET, null, USER_PAGE).getBody();
    }

    @Override
    public UserDto get(Long id) {
        return restTemplate.getForObject(backendBaseUrl + USERS_URL + "/{id}", UserDto.class, id);
    }

    @Override
    public UserDto create(UserDto user) {
        return restTemplate.postForObject(backendBaseUrl + USERS_URL, user, UserDto.class);
    }

    @Override
    public UserDto update(Long id, UserDto user) {
        return restTemplate.exchange(
                backendBaseUrl + USERS_URL + "/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(user),
                UserDto.class,
                id).getBody();
    }

    @Override
    public void delete(Long id) {
        restTemplate.delete(backendBaseUrl + USERS_URL + "/{id}", id);
    }

    @Override
    public Long count() {
        return restTemplate.getForObject(backendBaseUrl + USERS_URL + "/count", Long.class);
    }
}
