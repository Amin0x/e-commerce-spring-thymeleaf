package com.alamin.ecommerce.frontend.client.config;

import com.alamin.ecommerce.frontend.client.interceptor.ClientAuthInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for the frontend's REST client layer.
 *
 * <p>Builds a single {@link RestTemplate} (one per frontend module) with the
 * {@link ClientAuthInterceptor} registered so every outbound call to the
 * backend carries the authenticated user's JWT ({@code Authorization: Bearer})
 * and the caller's cart identity ({@code X-Cart-Key}).
 *
 * <p>The backend base URL comes from {@code backend.base-url} in
 * {@code frontend/src/main/resources/application.properties}.
 */
@Configuration
public class ClientConfig {

    @Value("${backend.base-url:http://localhost:8081}")
    private String backendBaseUrl;

    /** REST template wired with the auth/cart-key interceptor. */
    @Bean
    public RestTemplate restTemplate(ClientAuthInterceptor authInterceptor) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(authInterceptor);
        return restTemplate;
    }

    /** Expose the configured backend base URL for clients that need it explicitly. */
    @Bean
    public String backendBaseUrl() {
        return backendBaseUrl;
    }

    /** Registers the auth/cart-key interceptor as a Spring bean. */
    @Bean
    public ClientAuthInterceptor clientAuthInterceptor() {
        return new ClientAuthInterceptor();
    }
}
