package com.alamin.ecommerce.frontend.cart;

import com.alamin.ecommerce.frontend.client.api.CartClient;
import com.alamin.ecommerce.frontend.dto.CartDto;
import com.alamin.ecommerce.frontend.dto.ProductDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Public cart. Cart identity is resolved by the backend from the {@code X-Cart-Key}
 * header (guests) or the JWT principal (authenticated users); this controller
 * delegates to {@link CartClient} and lets {@link
 * com.alamin.ecommerce.frontend.client.interceptor.ClientAuthInterceptor}
 * supply the cart key and bearer token from the session.
 */
@Controller
public class CartController {

    private final CartClient cartClient;

    public CartController(CartClient cartClient) {
        this.cartClient = cartClient;
    }

    @GetMapping("/cart")
    public String showCartPage(Model model) {
        CartDto cart = cartClient.getCart();
        if (cart != null) {
            prefixImageUrls(cart.getCartItems());
        }
        model.addAttribute("cart", cart);
        model.addAttribute("pageDescription", "");
        model.addAttribute("pageAuthor", "");
        model.addAttribute("pageKeywords", "");
        model.addAttribute("pageTitle", "");
        return "public/cart";
    }

    @GetMapping("/carts/getCart")
    public ResponseEntity<Object> getCart() {
        CartDto cart = cartClient.getCart();
        Map<String, Object> data = new java.util.LinkedHashMap<>();
        if (cart == null) {
            data.put("cart", null);
            data.put("totalItems", 0);
            data.put("tax", 0);
            data.put("shipping", 0);
            data.put("status", 200);
            data.put("message", "cart found successfully");
            return ResponseEntity.ok(data);
        }
        List<?> items = cart.getCartItems();
        data.put("cart", cart);
        data.put("totalItems", items == null ? 0 : items.stream()
                .mapToInt(i -> ((com.alamin.ecommerce.frontend.dto.CartItemDto) i).getQuantity()).sum());
        data.put("tax", 0);
        data.put("shipping", 0);
        data.put("status", 200);
        data.put("message", "cart found successfully");
        return ResponseEntity.ok(data);
    }

    @PostMapping("/carts/addToCart")
    public ResponseEntity<Map<String, Object>> addItemToCart(@RequestParam String productId,
                                                             @RequestParam(defaultValue = "1") int quantity) {
        Map<String, Object> data = new java.util.LinkedHashMap<>();
        try {
            CartDto updatedCart = cartClient.addItem(productId, quantity);
            List<?> items = updatedCart == null ? null : updatedCart.getCartItems();
            data.put("status", 200);
            data.put("cart", updatedCart);
            data.put("totalItems", items == null ? 0 : items.stream()
                    .mapToInt(i -> ((com.alamin.ecommerce.frontend.dto.CartItemDto) i).getQuantity()).sum());
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            data.put("error", e.getMessage());
            data.put("status", 400);
            data.put("message", "something went wrong");
            return ResponseEntity.badRequest().body(data);
        }
    }

    @PostMapping("/carts/delete")
    public ResponseEntity<Object> removeCartItem(@RequestParam Long id) {
        Map<String, Object> data = new java.util.LinkedHashMap<>();
        try {
            CartDto cart = cartClient.removeItem(id);
            List<?> items = cart == null ? null : cart.getCartItems();
            data.put("cart", cart);
            data.put("totalItems", items == null ? 0 : items.stream()
                    .mapToInt(i -> ((com.alamin.ecommerce.frontend.dto.CartItemDto) i).getQuantity()).sum());
            data.put("tax", 0);
            data.put("shipping", 0);
            data.put("status", 200);
            data.put("message", "item removed from cart successfully");
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            data.put("error", e.getMessage());
            data.put("status", 400);
            data.put("message", "something went wrong");
            return ResponseEntity.badRequest().body(data);
        }
    }

    @PostMapping("/carts/updateQuantity")
    public ResponseEntity<Object> updateCartItemQuantity(@RequestParam Long id,
                                                          @RequestParam int quantity) {
        Map<String, Object> data = new java.util.LinkedHashMap<>();
        try {
            CartDto cart = cartClient.updateItemQuantity(id, quantity);
            List<?> items = cart == null ? null : cart.getCartItems();
            data.put("cart", cart);
            data.put("shipping", 0);
            data.put("tax", 0);
            data.put("status", 200);
            data.put("message", "item quantity updated successfully");
            data.put("totalItems", items == null ? 0 : items.stream()
                    .mapToInt(i -> ((com.alamin.ecommerce.frontend.dto.CartItemDto) i).getQuantity()).sum());
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            data.put("error", e.getMessage());
            data.put("status", 400);
            data.put("message", "something went wrong");
            return ResponseEntity.badRequest().body(data);
        }
    }

    private void prefixImageUrls(List<com.alamin.ecommerce.frontend.dto.CartItemDto> items) {
        if (items == null) {
            return;
        }
        for (com.alamin.ecommerce.frontend.dto.CartItemDto item : items) {
            ProductDto product = item.getProduct();
            if (product != null) {
                String image = product.getImage();
                if (image != null && !image.isBlank() && !image.startsWith("http")) {
                    product.setImage("/uploads/images/" + image);
                }
            }
        }
    }
}
