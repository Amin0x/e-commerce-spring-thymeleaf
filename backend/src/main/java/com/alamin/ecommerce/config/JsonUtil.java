package com.alamin.ecommerce.config;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tiny JSON serialization helper used by the REST authentication entry point
 * and access denied handler to write {@link com.alamin.ecommerce.exception.ApiError}
 * bodies without pulling Jackson into every security class.
 */
public final class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtil() {
    }

    public static String toJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to serialize JSON", e);
        }
    }
}
