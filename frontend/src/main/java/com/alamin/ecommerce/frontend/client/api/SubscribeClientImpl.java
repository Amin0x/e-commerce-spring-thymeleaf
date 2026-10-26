package com.alamin.ecommerce.frontend.client.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * {@link SubscribeClient} backed by {@link RestTemplate} against the backend
 * {@code /api/subscribe} endpoints.
 */
@Component
@RequiredArgsConstructor
public class SubscribeClientImpl implements SubscribeClient {

    private final RestTemplate restTemplate;
    private final String backendBaseUrl;

    @Override
    public Map<String, Object> subscribe(String email, String name) {
        var uri = UriComponentsBuilder.fromHttpUrl(backendBaseUrl + "/api/subscribe")
                .queryParam("email", email)
                .queryParam("name", name == null ? "" : name)
                .build().toUri();
        ResponseEntity<Map> response = restTemplate.postForEntity(uri, null, Map.class);
        return response.getBody() == null ? Map.of() : response.getBody();
    }

    @Override
    public Map<String, Object> unsubscribe(String email) {
        var uri = UriComponentsBuilder.fromHttpUrl(backendBaseUrl + "/api/subscribe/unsubscribe")
                .queryParam("email", email)
                .build().toUri();
        ResponseEntity<Map> response = restTemplate.postForEntity(uri, null, Map.class);
        return response.getBody() == null ? Map.of() : response.getBody();
    }

    @Override
    public Map<String, Object> verifyEmail(String email, String token) {
        var uri = UriComponentsBuilder.fromHttpUrl(backendBaseUrl + "/api/subscribe/verify")
                .queryParam("email", email)
                .queryParam("token", token)
                .build().toUri();
        ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);
        return response.getBody() == null ? Map.of() : response.getBody();
    }
}
