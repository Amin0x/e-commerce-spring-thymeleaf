package com.alamin.ecommerce.config;

import com.alamin.ecommerce.user.User;
import com.alamin.ecommerce.user.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Issues and validates HS256 JWTs with Nimbus JOSE (declared via
 * spring-security-oauth2-jose).
 *
 * <p>Claims: {@code sub}=username, {@code role}, {@code uid}=uuid, {@code iat},
 * {@code exp}, {@code jti}.
 *
 * <p>{@code app.jwt.secret} must be at least 256 bits (32 bytes) and is
 * overridable via the {@code JWT_SECRET} env var. A short or missing secret
 * fails fast at startup.
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    private final UserRepository userRepository;

    @Value("${app.jwt.access-token-expiration:86400000}")
    private long accessTokenExpiration;

    @Value("${app.jwt.secret:}")
    private String secret;

    private byte[] secretBytes;

    /** Fail fast if the configured secret is shorter than 256 bits (32 bytes). */
    @PostConstruct
    public void init() {
        String effective = System.getenv("JWT_SECRET");
        if (effective != null && !effective.isBlank()) {
            secret = effective;
        }
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("app.jwt.secret / JWT_SECRET is not configured");
        }
        secretBytes = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalStateException("app.jwt.secret / JWT_SECRET must be at least 256 bits (32 bytes)");
        }
    }

    private MACSigner signer() {
        try {
            return new MACSigner(secretBytes);
        } catch (JOSEException e) {
            throw new IllegalStateException("Unable to build JWT signer", e);
        }
    }

    private MACVerifier verifier() {
        try {
            return new MACVerifier(secretBytes);
        } catch (JOSEException e) {
            throw new IllegalStateException("Unable to build JWT verifier", e);
        }
    }

    public String generateToken(User user) {
        return generateToken(user.getUsername(), user.getRole(), user.getUuid());
    }

    public String generateToken(String username, String role, String uuid) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiration);
        try {
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(username)                                          // sub = username
                    .claim("role", role)                                        // role
                    .claim("uid", uuid)                                         // uid = uuid
                    .issueTime(now)                                             // iat
                    .expirationTime(expiry)                                     // exp
                    .jwtID(UUID.randomUUID().toString())                        // jti
                    .build();
            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            jwt.sign(signer());
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("Unable to sign JWT", e);
        }
    }

    private JWTClaimsSet parseClaims(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            if (!jwt.verify(verifier())) {
                throw new IllegalArgumentException("JWT signature verification failed");
            }
            return jwt.getJWTClaimsSet();
        } catch (ParseException | JOSEException e) {
            throw new IllegalArgumentException("Unable to parse JWT", e);
        }
    }

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return parseClaims(token).getExpirationTime().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return parseClaims(token).getStringClaim("role");
    }

    public String extractUid(String token) {
        return parseClaims(token).getStringClaim("uid");
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    public List<SimpleGrantedAuthority> extractAuthorities(String token) {
        String role = extractRole(token);
        if (role == null || role.isBlank()) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority(role));
    }

    public Set<String> extractRoles(String token) {
        String role = extractRole(token);
        return role == null || role.isBlank() ? Set.of() : Set.of(role);
    }

    /** Parses and verifies the supplied token, returning its claims set (used by the auth filter). */
    public JWTClaimsSet claimsForToken(String token) {
        return parseClaims(token);
    }
}
