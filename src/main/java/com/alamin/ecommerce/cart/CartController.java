package com.alamin.ecommerce.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/web/carts/{id}")
    public String showCart(@PathVariable Long id, Model model) {
        Cart cart = cartService.getCart(id);
        model.addAttribute(cart);
        return "cart";
    }
    
    @GetMapping("/api/carts/{id}")
    public ResponseEntity<Cart> getCart(@PathVariable Long id) {
        Cart cart = cartService.getCart(id);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/api/carts")
    public ResponseEntity<Cart> createCart(@RequestBody Cart cart) {
        Cart savedCart = cartService.saveCart(cart);
        return ResponseEntity.ok(savedCart);
    }

    @DeleteMapping("/api/carts/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long id) {
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/carts/{cartId}/items")
    public ResponseEntity<Cart> addItemToCart(@PathVariable Long cartId, @RequestBody CartItem item) {
        Cart updatedCart = cartService.addItemToCart(cartId, item);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/api/carts/{cartId}/items/{itemId}")
    public ResponseEntity<Cart> removeItemFromCart(@PathVariable Long cartId, @PathVariable Long itemId) {
        Cart updatedCart = cartService.removeItemFromCart(cartId, itemId);
        return ResponseEntity.ok(updatedCart);
    }
}
