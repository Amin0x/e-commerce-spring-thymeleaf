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
import java.util.HashMap;
import java.util.Map;

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
        Map<String, Object> data = new HashMap<>();
        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        Cart cart = cartService.getCartBySession(session);
        if (cart == null) {
            data.put("cart", null);
            data.put("totalItems", 0);
            data.put("tax", 0);
            data.put("shipping", 0);
            data.put("status", HttpStatus.BAD_REQUEST);
            data.put("message", "cart not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);            
        }

        CartDto cartDto = new CartDto(cart);
        
        data.put("cart", cartDto);
        data.put("totalItems", cartDto.getCartItems().stream().mapToInt(CartItemDto::getQuantity).sum());
        data.put("tax", 0);
        data.put("shipping", productService.getShipping(cart));
        data.put("status", HttpStatus.OK);
        data.put("message", "cart found successfully");
        return ResponseEntity.ok(data);
    }

    @PostMapping("/carts/addToCart")
    public ResponseEntity<Object> addItemToCart(@RequestParam Long productId,
                                              @RequestParam(defaultValue = "1") int quantity,
                                              HttpSession session, Principal principal) {
        Map<String, Object> data = new HashMap<>();
        try {
            Product product = productService.getProductById(productId).orElseThrow();
            Cart updatedCart = cartService.addCartItem(product, session, principal);
            CartDto updatedCartDto = new CartDto(updatedCart);
            
            data.put("cart", updatedCartDto);
            data.put("totalItems", updatedCartDto.getCartItems().stream().mapToInt(CartItemDto::getQuantity).sum());
        } catch (Exception e) {
            data.put("error", e.getMessage());
            data.put("status", HttpStatus.BAD_REQUEST);
            data.put("message", "something went wrong");
            log.error("Error adding item to cart: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
        }
        
        return ResponseEntity.ok(data);
    }

    @PostMapping("/carts/delete")
    public ResponseEntity<Object> removeCartItem(@RequestParam Long id, HttpSession session, Principal principal) {
        Cart cart = null;
        Map<String, Object> data = new HashMap<>();

        try {
            log.info("id: {}", id);
            cart = cartService.removeCartItem(id, session, principal);
        } catch (Exception e) {
            
            data.put("error", e.getMessage());
            data.put("status", HttpStatus.BAD_REQUEST);
            data.put("message", "something went wrong");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
        }
        data.put("cart", new CartDto(cart));
        data.put("totalItems", cart.getCartItems().stream().mapToInt(CartItem::getQuantity).sum());
        data.put("tax", 0);
        data.put("shipping", productService.getShipping(cart));
        data.put("status", HttpStatus.OK);
        data.put("message", "item removed from cart successfully");
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }


    @PostMapping("/carts/increment")
    public ResponseEntity<Object> cartItemIncrement(@RequestParam Long id, HttpSession session, Principal principal) {

        Cart cart = null;
        Map<String, Object> data = new HashMap<>();

        try {
            cart = cartService.incrementCartItem(id, session, principal);
        } catch (Exception e) {
            data.put("error", e.getMessage());
            data.put("status", HttpStatus.BAD_REQUEST);
            data.put("message", "something went wrong");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
        }

        data.put("cart", new CartDto(cart));
        data.put("shipping", productService.getShipping(cart));
        data.put("tax", 0);
        data.put("status", HttpStatus.OK);
        data.put("message", "item incremented successfully");
        data.put("totalItems", cart.getCartItems().stream().mapToInt(CartItem::getQuantity).sum());
        return ResponseEntity.ok(data);
    }

    @PostMapping("/carts/decrement")
    public ResponseEntity<Object> cartItemDecrement(@RequestParam Long id, HttpSession session, Principal principal) {
        Cart cart = null;
        Map<String, Object> data = new HashMap<>();

        try {
            cart = cartService.decrementCartItem(id, session, principal);
        } catch (Exception e) {
            data.put("error", e.getMessage());
            data.put("status", HttpStatus.BAD_REQUEST);
            data.put("message", "something went wrong");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
        }
        
        CartDto cartDto = new CartDto(cart);
        data.put("cart", cartDto);
        data.put("shipping", productService.getShipping(cart));
        data.put("tax", 0);
        data.put("status", HttpStatus.OK);
        data.put("message", "item decremented successfully");
        data.put("totalItems", cartDto.getCartItems().stream().mapToInt(CartItemDto::getQuantity).sum());
        return ResponseEntity.ok(data);
    }



}
