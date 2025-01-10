package com.alamin.ecommerce.cart;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/web/carts/{id}")
    public String showCart(@PathVariable Long id, Model model) {
        Cart cart = cartService.getCartById(id).orElseThrow();
        model.addAttribute("cart", cart);
        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "");
        return "public/cart";
    }
    
    @GetMapping("/api/carts/{id}")
    public ResponseEntity<Cart> getCart(@PathVariable Long id, HttpSession session) {

        Cart cart = cartService.getCartById(id).orElseThrow();
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

    @PostMapping("/api/carts/items")
    public ResponseEntity<Cart> addItemToCart(@RequestBody CartItem item) {
        Cart updatedCart = cartService.addItemToCart(item);
        return ResponseEntity.ok(updatedCart);
    }

    @PostMapping("/api/carts/items/{itemId}/delete")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long itemId) {
        try {
            cartService.deleteCartItem(itemId);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }






}
