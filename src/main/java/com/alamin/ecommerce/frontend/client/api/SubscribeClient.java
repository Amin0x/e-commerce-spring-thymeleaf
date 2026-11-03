package com.alamin.ecommerce.frontend.client.api;

import java.util.Map;

/**
 * Client for the backend subscription API ({@code /api/subscribe}).
 */
public interface SubscribeClient {

    Map<String, Object> subscribe(String email, String name);

    Map<String, Object> unsubscribe(String email);

    Map<String, Object> verifyEmail(String email, String token);
}
