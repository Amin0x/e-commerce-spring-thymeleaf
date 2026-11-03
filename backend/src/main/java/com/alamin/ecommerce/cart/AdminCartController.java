package com.alamin.ecommerce.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/cart")
@RequiredArgsConstructor
public class AdminCartController {

    private final CartService cartService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listAll(Pageable pageable) {
        return ResponseEntity.ok(cartService.findAll(pageable));
    }
}