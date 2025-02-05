package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.product.Product;
import com.alamin.ecommerce.product.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;

    @GetMapping("/cart")
    public String showCartPage(Model model, HttpSession session) {
        Cart cart = cartService.getCartBySession(session);
        model.addAttribute("cart", cart);
        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "");
        return "public/cart";
    }
    
    @GetMapping("/api/cart/get")
    public ResponseEntity<Cart> getCart(HttpSession session) {
        Cart cart = cartService.getCartBySession(session);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/api/carts/add")
    public ResponseEntity<Cart> addItemToCart(@RequestParam Long productId,
                                              @RequestParam int quantity,
                                              HttpSession session, Principal principal) {
        Product product = productService.getProductById(productId).orElseThrow();
        Cart updatedCart = cartService.addItemToCart(product, session, principal);
        return ResponseEntity.ok(updatedCart);
    }

    @PostMapping("/api/carts/delete/{id}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long id, HttpSession session, Principal principal) {
        try {
            cartService.removeCartItem(id, session, principal);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }






}
