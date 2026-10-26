package com.alamin.ecommerce.subscribe;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class SubscribeController {
    private final SubscribeService subscribeService;

    public SubscribeController(SubscribeService subscribeService) {
        this.subscribeService = subscribeService;
    }

    @GetMapping("/subscription/subscribe")
    public ResponseEntity<Map<String, Object>> subscribe(@RequestParam String name, @RequestParam String email){
        subscribeService.subscribe(email, name);
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("status", HttpStatus.OK);
            response.put("message", "subscribed success");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException ex) {
            response.put("status", HttpStatus.BAD_REQUEST);
            response.put("message", ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            response.put("status", HttpStatus.BAD_REQUEST);
            response.put("message", "subscribed success");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/subscription/unsubscribe")
    public ResponseEntity<Object> unsubscribe(@RequestParam String email){
        subscribeService.unsubscribe(email);
        return ResponseEntity.status(200).build();
    }

    @GetMapping("/subscription/verifyEmail")
    public ResponseEntity<Map<String, Object>> verifyEmail(@RequestParam String email,@RequestParam String token){
        try {
            subscribeService.verifyEmail(email, token);
            return ResponseEntity.status(200).body(Map.of("status", HttpStatus.OK, "message", "email verified"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("status", HttpStatus.BAD_REQUEST, "message", e.getMessage()));
        }
    }
}
