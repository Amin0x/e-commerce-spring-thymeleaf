package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.product.Product;
import com.alamin.ecommerce.product.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
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
    
    @GetMapping("/carts/getCart")
    public ResponseEntity<Object> getCart(HttpSession session) {
        Cart cart = cartService.getCartBySession(session);
        if (cart == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        CartDto cartDto = new CartDto(cart);
        return ResponseEntity.ok(cartDto);
    }

    @PostMapping("/carts/addToCart")
    public ResponseEntity<CartDto> addItemToCart(@RequestParam Long productId,
                                              @RequestParam int quantity,
                                              HttpSession session, Principal principal) {
        Product product = productService.getProductById(productId).orElseThrow();
        Cart updatedCart = cartService.addItemToCart(product, session, principal);
        CartDto updatedCartDto = new CartDto(updatedCart);
        return ResponseEntity.ok(updatedCartDto);
    }

    @PostMapping("/carts/delete")
    public ResponseEntity<Void> removeCartItem(@RequestParam Long id, HttpSession session, Principal principal) {
        try {
            log.info("id: {}", id);
            cartService.removeCartItem(id, session, principal);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }


    @PostMapping("/carts/increment")
    public ResponseEntity<Object> cartItemIncrement(@RequestParam Long id, HttpSession session, Principal principal) {
       
        try {
            cartService.incrementCartItem(id, session, principal);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PostMapping("/carts/decrement")
    public ResponseEntity<Object> cartItemDecrement(@RequestParam Long id, HttpSession session, Principal principal) {
        try {
            cartService.decrementCartItem(id, session, principal);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }



}
