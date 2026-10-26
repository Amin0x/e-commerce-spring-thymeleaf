package com.alamin.ecommerce.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Maps the JWT {@code role} claim to a bare {@link SimpleGrantedAuthority(role)}
 * (no "ROLE_" prefix), exactly as the backend security contract requires.
 *
 * <p>Registered as a bean so it can be plugged into Spring OAuth2 Resource
 * Server via {@code oauth2ResourceServer(jwt -> jwt.jwtAuthenticationConverter(...))}
 * if the project later switches from the custom {@link JwtAuthenticationFilter}
 * to the resource-server filter. The secret length check keeps the fail-fast
 * {@code app.jwt.secret} contract consistent with {@link JwtService}.
 */
@Component
public class JwtAuthenticationConverter
        implements Converter<Jwt, Collection<? extends GrantedAuthority>> {

    @Value("${app.jwt.secret:}")
    private String secret;

    @PostConstruct
    public void init() {
        String effective = System.getenv("JWT_SECRET");
        String value = (effective != null && !effective.isBlank()) ? effective : secret;
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("app.jwt.secret / JWT_SECRET is not configured");
        }
        if (value.getBytes(java.nio.charset.StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "app.jwt.secret / JWT_SECRET must be at least 256 bits (32 bytes)");
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> convert(Jwt jwt) {
        Object role = jwt.getClaims().get("role");
        if (role == null) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority(role.toString()));
    }
}
