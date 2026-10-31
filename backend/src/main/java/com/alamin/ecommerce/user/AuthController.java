package com.alamin.ecommerce.user;

import com.alamin.ecommerce.config.JwtService;
import com.alamin.ecommerce.exception.ResourceAlreadyExistsException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Stateless REST authentication controller for the backend API.
 *
 * Endpoints:
 *   POST /api/auth/login     - authenticate credentials, issue a JWT
 *   POST /api/auth/register  - create a new USER account
 *   POST /api/auth/logout    - stateless no-op (client drops the token)
 *   GET  /api/auth/me        - return the currently authenticated user
 *
 * Identity always comes from the verified JWT via the security context
 * (populated by {@link com.alamin.ecommerce.config.JwtAuthenticationFilter}),
 * never from a caller-supplied string — closing the IDOR vector flagged for
 * {@code CartServiceImpl}.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    /** Authenticate username/password and issue a JWT. */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var userOpt = userService.getUserByName(request.username());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var user = userOpt.get();
        String token = jwtService.generateToken(user);

        AuthResponse response = new AuthResponse(
                token,
                user.getUsername(),
                user.getRole(),
                user.getUuid(),
                "Login successful");
        return ResponseEntity.ok(response);
    }

    /** Register a new user (defaults to USER role). */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.getUserByUsername(request.username()).isPresent()) {
            throw new ResourceAlreadyExistsException("Username already taken: " + request.username());
        }
        if (request.email() != null && userRepository.findByEmail(request.email()).isPresent()) {
            throw new ResourceAlreadyExistsException("Email already taken: " + request.email());
        }

        // Self-registration is always USER. Role assignment for others is an
        // admin-only operation (handled by AdminUserController), never a
        // client-controlled field here — prevents privilege escalation.
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole("USER");
        user.setEmail(request.email());
        user.setEnabled(true);
        user.setStatus("NOT_VERIFIED");
        user.setUuid(java.util.UUID.randomUUID().toString().replace("-", ""));
        user.setCreated(LocalDateTime.now());
        user.setUpdated(LocalDateTime.now());

        userService.createUser(user);

        String token = jwtService.generateToken(user);
        AuthResponse response = new AuthResponse(
                token,
                user.getUsername(),
                user.getRole(),
                user.getUuid(),
                "Registration successful");
        return ResponseEntity.ok(response);
    }

    /** Stateless logout: the client simply discards the JWT. Returned for API symmetry. */
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout() {
        SecurityContextHolder.clearContext();
        AuthResponse response = new AuthResponse(null, null, null, null, "Logged out");
        return ResponseEntity.ok(response);
    }

    /** Return the currently authenticated user's profile. */
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        }
        var userOpt = userService.getUserByName(authentication.getName());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND).build();
        }
        var user = userOpt.get();
        AuthResponse response = new AuthResponse(
                null,
                user.getUsername(),
                user.getRole(),
                user.getUuid(),
                "Authenticated");
        return ResponseEntity.ok(response);
    }
}
