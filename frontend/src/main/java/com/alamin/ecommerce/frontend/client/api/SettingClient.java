package com.alamin.ecommerce.frontend.client.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Client for the backend settings endpoints ({@code /api/settings/home/category}).
 */
@Component
@RequiredArgsConstructor
public class SettingClient {

    private final RestTemplate restTemplate;
    private final String backendBaseUrl;

    public void addCategoryToHome() {
        restTemplate.postForEntity(backendBaseUrl + "/api/settings/home/category", null, Void.class);
    }

    public ResponseEntity<Void> removeCategoryFromHome() {
        return restTemplate.exchange(
                backendBaseUrl + "/api/settings/home/category",
                org.springframework.http.HttpMethod.DELETE, null, Void.class);
    }
}
