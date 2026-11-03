package com.alamin.ecommerce.frontend.subscribe;

import com.alamin.ecommerce.frontend.client.api.SubscribeClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Public subscription. Delegates to the backend {@code /api/subscribe} API
 * through {@link SubscribeClient}.
 */
@Controller
public class SubscribeController {

    private final SubscribeClient subscribeClient;

    public SubscribeController(SubscribeClient subscribeClient) {
        this.subscribeClient = subscribeClient;
    }

    @GetMapping("/subscription/subscribe")
    public ResponseEntity<Map<String, Object>> subscribe(@RequestParam String name, @RequestParam String email) {
        Map<String, Object> response = subscribeClient.subscribe(email, name);
        if (response.containsKey("failed")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/subscription/unsubscribe")
    public ResponseEntity<Object> unsubscribe(@RequestParam String email) {
        subscribeClient.unsubscribe(email);
        return ResponseEntity.status(200).build();
    }

    @GetMapping("/subscription/verifyEmail")
    public ResponseEntity<Map<String, Object>> verifyEmail(@RequestParam String email,
                                                           @RequestParam String token) {
        Map<String, Object> response = subscribeClient.verifyEmail(email, token);
        if (response.containsKey("failed")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
