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
        Cart cart = cartService.getCartBySession(session);
        if (cart == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        CartDto cartDto = new CartDto(cart);
        Map<String, Object> data = new HashMap<>();
        data.put("cart", cartDto);
        data.put("totalItems", cartDto.getCartItems().stream().mapToInt(CartItemDto::getQuantity).sum());
        data.put("tax", 0);
        data.put("shipping", productService.getShipping(cart));
        return ResponseEntity.ok(data);
    }

    @PostMapping("/carts/addToCart")
    public ResponseEntity<Object> addItemToCart(@RequestParam Long productId,
                                              @RequestParam(defaultValue = "1") int quantity,
                                              HttpSession session, Principal principal) {
        Product product = productService.getProductById(productId).orElseThrow();
        Cart updatedCart = cartService.addItemToCart(product, session, principal);
        CartDto updatedCartDto = new CartDto(updatedCart);
        Map<String, Object> data = new HashMap<>();
        data.put("cart", updatedCartDto);
        data.put("totalItems", updatedCartDto.getCartItems().stream().mapToInt(CartItemDto::getQuantity).sum());
        return ResponseEntity.ok(data);
    }

    @PostMapping("/carts/delete")
    public ResponseEntity<Object> removeCartItem(@RequestParam Long id, HttpSession session, Principal principal) {
        Cart cart = null;
        try {
            log.info("id: {}", id);
            cart = cartService.removeCartItem(id, session, principal);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(new CartDto(cart));
    }


    @PostMapping("/carts/increment")
    public ResponseEntity<Object> cartItemIncrement(@RequestParam Long id, HttpSession session, Principal principal) {

        Cart cart = null;
        try {
            cart = cartService.incrementCartItem(id, session, principal);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> data = new HashMap<>();
        CartDto cartDto = new CartDto(cart);
        data.put("cart", cartDto);
        data.put("totalItems", cartDto.getCartItems().stream().mapToInt(CartItemDto::getQuantity).sum());
        return ResponseEntity.ok(data);
    }

    @PostMapping("/carts/decrement")
    public ResponseEntity<Object> cartItemDecrement(@RequestParam Long id, HttpSession session, Principal principal) {
        Cart cart = null;
        try {
            cart = cartService.decrementCartItem(id, session, principal);
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("something went wrong");
        }
        Map<String, Object> data = new HashMap<>();
        CartDto cartDto = new CartDto(cart);
        data.put("cart", cartDto);
        data.put("totalItems", cartDto.getCartItems().stream().mapToInt(CartItemDto::getQuantity).sum());
        return ResponseEntity.ok(data);
    }



}
