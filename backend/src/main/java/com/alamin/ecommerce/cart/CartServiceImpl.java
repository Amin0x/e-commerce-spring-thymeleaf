package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.product.Product;
import com.alamin.ecommerce.product.ProductDto;
import com.alamin.ecommerce.product.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Stateless cart service. Identities are resolved at the REST-controller boundary
 * (Tasks 16-22): {@code username} is derived from the authenticated JWT principal
 * ({@code Authentication.getName()} / {@code sub} claim) and {@code cartKey} is an
 * opaque server-issued key — never trusted raw caller input, which would be an
 * IDOR / identity-spoofing vector. The service performs no trust boundary itself;
 * controllers must validate ownership before delegating.
 *
 * <p>All cart-returning methods expose {@link CartDto} rather than the JPA entity
 * so persistence concerns never leak out of the service layer.
 */
@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository,
                           ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Optional<CartDto> getCartById(Long id) {
        return cartRepository.findById(id).map(CartDto::new);
    }

    @Override
    public CartDto getCartBySession(String cartKey) {
        return new CartDto(requireCartBySession(cartKey));
    }

    @Override
    public CartDto getCartByUserId(String userId) {
        return new CartDto(requireCartByUserId(userId));
    }

    @Override
    public CartDto saveCart(Cart cart) {
        return new CartDto(cartRepository.save(cart));
    }

    @Override
    public void deleteCart(Long id) {
        cartRepository.deleteById(id);
    }

    @Override
    public CartDto addCartItem(ProductDto item, String cartKey, String username) {
        Cart cart = resolveCart(cartKey, username);

        // Fetch the Product entity for JPA relationship
        Product product = productRepository.findById(item.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + item.productId()));

        boolean found = false;
        for (var ci : cart.getCartItems()) {
            if (Objects.equals(ci.getProduct().getId(), product.getId())) {
                ci.setQuantity(ci.getQuantity() + 1);
                ci.setTotal(ci.getQuantity() * ci.getPrice());
                ci.setPrice(product.getPrice());
                found = true;
            }
        }

        if (!found) {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cartItem.setPrice(product.getPrice());
            cartItem.setTotal(product.getPrice() * cartItem.getQuantity());

            cart.getCartItems().add(cartItem);
        }

        int total = 0;
        for (var it : cart.getCartItems()) {
            total += it.getQuantity() * it.getPrice();
        }
        cart.setTotal(total);
        return saveCart(cart);
    }

    @Override
    public CartDto removeCartItem(Long id, String cartKey, String username) {
        Cart cart = resolveCart(cartKey, username);

        List<CartItem> cartItems = cart.getCartItems();
        boolean found = false;
        for (var ci : cartItems) {
            if (Objects.equals(ci.getId(), id)) {
                cartItems.remove(ci);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new RuntimeException("Item not found in cart");
        }
        return saveCart(cart);
    }

    @Override
    public Page<CartDto> findAll(Pageable pr) {
        return cartRepository.findAll(Objects.requireNonNullElseGet(pr, () -> PageRequest.ofSize(20)))
                .map(CartDto::new);
    }

    @Override
    public boolean existsById(Long id) {
        try {
            return cartRepository.existsById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CartDto updateCartItemQuantity(Long id, String cartKey, String username, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }

        Cart cart = resolveCart(cartKey, username);

        boolean found = false;
        for (var ci : cart.getCartItems()) {
            if (Objects.equals(ci.getId(), id)) {
                ci.setQuantity(quantity);
                ci.setTotal(ci.getQuantity() * ci.getPrice());
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Cart item not found with id: " + id);
        }

        int total = 0;
        for (var it : cart.getCartItems()) {
            total += it.getQuantity() * it.getPrice();
        }
        cart.setTotal(total);
        return saveCart(cart);
    }

    @Override
    public CartDto getOrCreateCart(String cartKey, String username) {
        if (username != null && !username.isEmpty()) {
            try {
                Cart existingCart = requireCartByUserId(username);
                return new CartDto(cartRepository.findById(existingCart.getId()).orElse(existingCart));
            } catch (IllegalStateException e) {
                Cart cart = new Cart();
                cart.setUserId(username);
                cart.setSessionId(cartKey);
                return saveCart(cart);
            }
        } else {
            try {
                return new CartDto(requireCartBySession(cartKey));
            } catch (IllegalStateException e) {
                Cart cart = new Cart();
                cart.setSessionId(cartKey);
                return saveCart(cart);
            }
        }
    }

    private Cart resolveCart(String cartKey, String username) {
        return (username != null && !username.isEmpty())
                ? requireCartByUserId(username)
                : requireCartBySession(cartKey);
    }

    private Cart requireCartBySession(String cartKey) {
        if (cartKey == null || cartKey.isEmpty()) {
            throw new IllegalArgumentException("cart key is required");
        }
        Cart cart = cartRepository.getCartBySession(cartKey);
        if (cart == null) {
            throw new IllegalStateException("Cart not found for session: " + cartKey);
        }
        return cart;
    }

    private Cart requireCartByUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("user id is required");
        }
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            throw new IllegalStateException("Cart not found for user: " + userId);
        }
        return cart;
    }
}
