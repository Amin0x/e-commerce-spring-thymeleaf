package com.alamin.ecommerce.config;

import com.alamin.ecommerce.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Extracts and validates a Bearer JWT from the {@code Authorization} header on
 * every request. When valid, builds a {@link UsernamePasswordAuthenticationToken}
 * from the JWT claims ({@code sub}=username, {@code role} granted as a bare
 * authority) and places it in the security context so downstream controllers
 * can resolve the authenticated user via {@code Principal} /
 * {@code Authentication}.
 *
 * <p>Identity is never accepted from request parameters — it always comes from
 * a verified JWT — which closes the IDOR / identity-spoofing vector flagged
 * for {@link CartServiceImpl}.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer ";

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith(BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(BEARER.length());
        try {
            username = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            // Malformed / expired / unsigned token -> treat as unauthenticated.
            filterChain.doFilter(request, response);
            return;
        }

        if (username != null && !username.isBlank()
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            boolean valid = jwtService.isTokenValid(jwt);
            if (valid) {
                var userOpt = jwtService.findUserByUsername(username);
                if (userOpt.isEmpty()) {
                    // User no longer exists -> treat as unauthenticated.
                    filterChain.doFilter(request, response);
                    return;
                }
                User user = userOpt.get();
                // Do not trust the JWT role claim blindly: reject tokens whose
                // role no longer matches the persisted user (counters stale /
                // forged role claims after a role change).
                String jwtRole = jwtService.extractRole(jwt);
                if (jwtRole == null || !jwtRole.equals(user.getRole())) {
                    filterChain.doFilter(request, response);
                    return;
                }
                if (!Boolean.TRUE.equals(user.getEnabled())) {
                    // Disabled users cannot authenticate.
                    filterChain.doFilter(request, response);
                    return;
                }
                UserDetailsAdapter userDetails = new UserDetailsAdapter(user);
                List<org.springframework.security.core.GrantedAuthority> authorities =
                        jwtService.extractAuthorities(jwt);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Thin {@code UserDetails} wrapper around the domain {@link User} so the
     * authentication token carries a principal Spring Security recognizes.
     */
    public record UserDetailsAdapter(User user)
            implements org.springframework.security.core.userdetails.UserDetails {

        @Override
        public java.util.Collection<? extends org.springframework.security.core.GrantedAuthority>
                getAuthorities() {
            return java.util.List.of(
                    new org.springframework.security.core.authority.SimpleGrantedAuthority(user.getRole()));
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        @Override
        public String getUsername() {
            return user.getUsername();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return Boolean.TRUE.equals(user.getEnabled());
        }
    }
}
