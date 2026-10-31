package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.product.ProductDto;
import com.alamin.ecommerce.product.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
        Cart cart = cartService.resolveOrCreateCart(cartKey, username);
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/items")
    public ResponseEntity<?> getCartItems(HttpServletRequest request,
                                          @AuthenticationPrincipal UserDetails principal) {
        String cartKey = request.getHeader("X-Cart-Key");
        String username = principal == null ? null : principal.getUsername();
        Cart cart = cartService.resolveOrCreateCart(cartKey, username);
        return ResponseEntity.ok(cart.getCartItems());
    }

    @PostMapping("/items")
    public ResponseEntity<?> addItem(@RequestParam String productId,
                                     @RequestParam(defaultValue = "1") int quantity,
                                     HttpServletRequest request,
                                     @AuthenticationPrincipal UserDetails principal) {
        String cartKey = request.getHeader("X-Cart-Key");
        String username = principal == null ? null : principal.getUsername();
        ProductDto product = productService.getProductByUuid(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        Cart cart = cartService.addCartItem(product, cartKey, username);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> removeItem(@PathVariable Long id,
                                        HttpServletRequest request,
                                        @AuthenticationPrincipal UserDetails principal) {
        String cartKey = request.getHeader("X-Cart-Key");
        String username = principal == null ? null : principal.getUsername();
        Cart cart = cartService.removeCartItem(id, cartKey, username);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items/{id}/increment")
    public ResponseEntity<?> increment(@PathVariable Long id,
                                       HttpServletRequest request,
                                       @AuthenticationPrincipal UserDetails principal) {
        String cartKey = request.getHeader("X-Cart-Key");
        String username = principal == null ? null : principal.getUsername();
        Cart cart = cartService.incrementCartItem(id, cartKey, username);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items/{id}/decrement")
    public ResponseEntity<?> decrement(@PathVariable Long id,
                                       HttpServletRequest request,
                                       @AuthenticationPrincipal UserDetails principal) {
        String cartKey = request.getHeader("X-Cart-Key");
        String username = principal == null ? null : principal.getUsername();
        Cart cart = cartService.decrementCartItem(id, cartKey, username);
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/admin")
    public ResponseEntity<?> listAll(Pageable pageable) {
        return ResponseEntity.ok(cartService.findAll(pageable));
    }

}
