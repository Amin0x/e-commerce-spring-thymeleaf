package com.alamin.ecommerce.frontend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Frontend (Thymeleaf UI) security. Public catalog, cart, auth and static
 * resources are open; {@code /admin/**} requires {@code ROLE_ADMIN}.
 * <p>
 * Authentication is session-backed: {@link SessionAuthenticationFilter} reads
 * the JWT and role from the servlet session each request, so Spring Security's
 * own stateful session is unnecessary.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           SessionAuthenticationFilter sessionAuthFilter) throws Exception {
        http
            // The frontend forwards credentials to the backend API and keeps the
            // JWT in the servlet session, so CSRF tokens are not issued here.
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/",
                        "/auth/**",
                        "/cart/**",
                        "/carts/**",
                        "/help",
                        "/offers",
                        "/about",
                        "/faq",
                        "/privacy",
                        "/return-policy",
                        "/return_policy",
                        "/products/**",
                        "/categories/**",
                        "/subscription/**",
                        "/checkout",
                        "/css/**", "/js/**", "/images/**", "/uploads/**",
                        "/public/**",
                        "/favicon.ico", "/error/**"
                ).permitAll()
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(sessionAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
