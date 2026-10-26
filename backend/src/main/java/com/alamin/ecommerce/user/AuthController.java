package com.alamin.ecommerce.user;

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
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var userOpt = userService.getUserByName(request.getUsername());
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
        if (userRepository.getUserByUsername(request.getUsername()).isPresent()) {
            throw new ResourceAlreadyExistsException("Username already taken: " + request.getUsername());
        }
        if (request.getEmail() != null && userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Email already taken: " + request.getEmail());
        }

        String role = request.getRole() == null || request.getRole().isBlank() ? "USER" : request.getRole();

        var user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setEmail(request.getEmail());
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
