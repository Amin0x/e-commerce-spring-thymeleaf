package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.product.Product;
import com.alamin.ecommerce.product.ProductRepository;
import com.alamin.ecommerce.product.ProductService;
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
    @Autowired
    private ProductService productService;

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

    
    @PostMapping("/api/carts/items")
    public ResponseEntity<Cart> addItemToCart(@RequestBody Long productId, HttpSession session) {
        Product product = productService.getProductById(productId).orElseThrow();
        Cart updatedCart = cartService.addItemToCart(product, session.getId());
        return ResponseEntity.ok(updatedCart);
    }

    @PostMapping("/api/carts/items/{itemId}/delete")
    public ResponseEntity<Void> reomveCartItem(@PathVariable Long itemId, HttpSession session) {
        try {
            cartService.removeCartItem(itemId, session.getId());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }






}
