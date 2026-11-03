package com.alamin.ecommerce.frontend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Populates the Spring Security context on each request from the servlet
 * session, which holds the JWT and the user role after login/register
 * (see {@link com.alamin.ecommerce.frontend.client.api.AuthClientImpl}).
 * <p>
 * The frontend does not validate the JWT itself — the backend does. The filter
 * treats the presence of a session token as authenticated and grants
 * {@code ROLE_ADMIN} when the stored role is {@code ADMIN}, so admin routes can
 * be guarded with {@code hasAuthority('ADMIN')}.
 */
@Component
public class SessionAuthenticationFilter extends OncePerRequestFilter {

    private static final String JWT_TOKEN_ATTR = "jwtToken";
    private static final String ROLE_ATTR = "authRole";
    private static final String USERNAME_ATTR = "authUsername";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        var attrs = RequestContextHolder.getRequestAttributes();
        String token = null;
        String role = null;
        String username = null;
        if (attrs instanceof ServletRequestAttributes sra) {
            var session = sra.getRequest().getSession(false);
            if (session != null) {
                token = (String) session.getAttribute(JWT_TOKEN_ATTR);
                role = (String) session.getAttribute(ROLE_ATTR);
                username = (String) session.getAttribute(USERNAME_ATTR);
            }
        }

        if (token != null && !token.isBlank()) {
            List<SimpleGrantedAuthority> authorities = "ADMIN".equalsIgnoreCase(role)
                    ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                    : List.of(new SimpleGrantedAuthority("ROLE_USER"));
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, token, authorities);
            authentication.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
