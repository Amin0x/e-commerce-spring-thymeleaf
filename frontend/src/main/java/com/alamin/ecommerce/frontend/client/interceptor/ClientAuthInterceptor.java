package com.alamin.ecommerce.frontend.client.interceptor;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.UUID;

/**
 * Intercepts every outbound {@link RestTemplate} call to the backend and injects:
 *
 * <ol>
 *   <li>{@code Authorization: Bearer <jwt>} — the JWT obtained from
 *       {@code POST /api/auth/login}, stored in the HTTP session under {@link #JWT_TOKEN_ATTR}.
 *       When no token is present (guest browsing) the header is simply omitted so
 *       public catalog endpoints still work.</li>
 *   <li>{@code X-Cart-Key <cartKey>} — a stable, server-neutral cart identity.
 *       Generated once per session and stored under {@link #CART_KEY_ATTR}. Guests and
 *       authenticated users alike carry the same cart key within a session; the
 *       backend resolves the cart from {@code X-Cart-Key} (guests) or the JWT
 *       principal ({@code sub} = username, authenticated users).</li>
 * </ol>
 *
 * <p>The interceptor runs inside a dispatcher-servlet request thread, so it
 * resolves the current session through {@link RequestContextHolder}. Calls made
 * outside an HTTP request (e.g. scheduled tasks) degrade gracefully: no token,
 * and a fresh random cart key per call.
 */
public class ClientAuthInterceptor implements ClientHttpRequestInterceptor {

    public static final String JWT_TOKEN_ATTR = "jwtToken";
    public static final String CART_KEY_ATTR = "cartKey";

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String CART_KEY_HEADER = "X-Cart-Key";

    @Value("${backend.base-url:http://localhost:8081}")
    private String backendBaseUrl; // reserved: kept for future allow-list filtering

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        addAuthHeader(request);
        addCartKeyHeader(request);
        return execution.execute(request, body);
    }

    private void addAuthHeader(HttpRequest request) {
        String token = resolveToken();
        if (token != null && !token.isBlank()) {
            request.getHeaders().add(AUTH_HEADER, BEARER_PREFIX + token);
        }
    }

    private void addCartKeyHeader(HttpRequest request) {
        String cartKey = resolveCartKey();
        if (cartKey != null) {
            request.getHeaders().add(CART_KEY_HEADER, cartKey);
        }
    }

    private String resolveToken() {
        HttpSession session = currentSession();
        if (session == null) {
            return null;
        }
        Object attr = session.getAttribute(JWT_TOKEN_ATTR);
        return attr == null ? null : String.valueOf(attr);
    }

    private String resolveCartKey() {
        HttpSession session = currentSession();
        if (session == null) {
            return UUID.randomUUID().toString();
        }
        Object attr = session.getAttribute(CART_KEY_ATTR);
        if (attr == null) {
            String cartKey = UUID.randomUUID().toString();
            session.setAttribute(CART_KEY_ATTR, cartKey);
            return cartKey;
        }
        return String.valueOf(attr);
    }

    @SuppressWarnings("unchecked")
    private HttpSession currentSession() {
        try {
            var attrs = RequestContextHolder.getRequestAttributes();
            if (attrs == null || !(attrs instanceof ServletRequestAttributes)) {
                return null;
            }
            return ((ServletRequestAttributes) attrs).getRequest().getSession(false);
        } catch (IllegalStateException e) {
            // No request bound to the current thread.
            return null;
        }
    }
}
