package com.alamin.ecommerce.config;

import com.alamin.ecommerce.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Returns a JSON {@link ApiError} (401) instead of a redirect to a login page,
 * appropriate for a stateless REST backend.
 */
@Slf4j
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.debug("Authentication entry point: {}", authException.getMessage());
        ApiError error = new ApiError(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Authentication required",
                request.getRequestURI());
        writeJson(response, HttpStatus.UNAUTHORIZED, error);
    }

    private static void writeJson(HttpServletResponse response, HttpStatus status, ApiError error)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        try (OutputStream os = response.getOutputStream()) {
            os.write(JsonUtil.toJson(error).getBytes("UTF-8"));
        }
    }
}
