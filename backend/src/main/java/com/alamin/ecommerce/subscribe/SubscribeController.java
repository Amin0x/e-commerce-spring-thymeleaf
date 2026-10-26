package com.alamin.ecommerce.subscribe;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Subscription REST endpoint. Delegates to {@link SubscribeService}; returns
 * JSON instead of rendering views.
 */
@RestController
@RequestMapping("/api/subscribe")
@RequiredArgsConstructor
public class SubscribeController {

    private final SubscribeService subscribeService;

    @PostMapping
    public ResponseEntity<?> subscribe(@RequestParam String email,
                                       @RequestParam(required = false) String name) {
        try {
            subscribeService.subscribe(email, name == null ? "" : name);
            return ResponseEntity.ok(Map.of("status", "subscribed", "email", email));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "failed", "message", e.getMessage()));
        }
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribe(@RequestParam String email) {
        try {
            subscribeService.unsubscribe(email);
            return ResponseEntity.ok(Map.of("status", "unsubscribed", "email", email));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "failed", "message", e.getMessage()));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String email, @RequestParam String token) {
        try {
            subscribeService.verifyEmail(email, token);
            return ResponseEntity.ok(Map.of("status", "verified", "email", email));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "failed", "message", e.getMessage()));
        }
    }
}
