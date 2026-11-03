package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.product.ProductDto;
import com.alamin.ecommerce.product.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


/**
 * Stateless REST cart. Cart identity comes from the {@code X-Cart-Key} header
 * for guests and from the authenticated principal ({@code sub} = username) for
 * logged-in users — never from a caller-supplied username, which keeps the
 * IDOR boundary at the controller/service contract.
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final ProductService productService;

    /** Current cart for the caller (guest key or authenticated user). */
    @GetMapping
    public ResponseEntity<?> getCart(HttpServletRequest request,
                                     @AuthenticationPrincipal UserDetails principal) {
        String cartKey = request.getHeader("X-Cart-Key");
        String username = principal == null ? null : principal.getUsername();
        CartDto cart = cartService.getOrCreateCart(cartKey, username);
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/items")
    public ResponseEntity<?> getCartItems(HttpServletRequest request,
                                          @AuthenticationPrincipal UserDetails principal) {
        String cartKey = request.getHeader("X-Cart-Key");
        String username = principal == null ? null : principal.getUsername();
        CartDto cart = cartService.getOrCreateCart(cartKey, username);
        return ResponseEntity.ok(cart.getCartItems());
    }

    @PostMapping("/items")
    public ResponseEntity<?> addItem(@RequestParam String productId,
                                     @RequestParam(defaultValue = "1") int quantity,
                                     HttpServletRequest request,
                                     @AuthenticationPrincipal UserDetails principal) {
        String cartKey = request.getHeader("X-Cart-Key");
        String username = principal == null ? null : principal.getUsername();

        // Ensure cart exists
        cartService.getOrCreateCart(cartKey, username);

        ProductDto product = productService.getProductByUuid(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        CartDto cart = cartService.addCartItem(product, cartKey, username);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> removeItem(@PathVariable Long id,
                                        HttpServletRequest request,
                                        @AuthenticationPrincipal UserDetails principal) {
        String cartKey = request.getHeader("X-Cart-Key");
        String username = principal == null ? null : principal.getUsername();

        // Ensure cart exists
        cartService.getOrCreateCart(cartKey, username);

        CartDto cart = cartService.removeCartItem(id, cartKey, username);
        return ResponseEntity.ok(cart);
    }

    @PatchMapping("/items/{id}")
    public ResponseEntity<?> updateItemQuantity(@PathVariable Long id,
                                                @RequestParam int quantity,
                                                HttpServletRequest request,
                                                @AuthenticationPrincipal UserDetails principal) {
        String cartKey = request.getHeader("X-Cart-Key");
        String username = principal == null ? null : principal.getUsername();

        // Ensure cart exists
        cartService.getOrCreateCart(cartKey, username);

        CartDto cart = cartService.updateCartItemQuantity(id, cartKey, username, quantity);
        return ResponseEntity.ok(cart);
    }

}
