package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.product.Product;
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
 */
@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public Optional<Cart> getCartById(Long id) {
        return cartRepository.findById(id);
    }

    @Override
    public Cart getCartBySession(String cartKey) {
        if (cartKey == null || cartKey.isEmpty()) {
            throw new IllegalArgumentException("cart key is required");
        }
        return cartRepository.getCartBySession(cartKey);
    }

    @Override
    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }

    @Override
    public void deleteCart(Long id) {
        cartRepository.deleteById(id);
    }

    @Override
    public Cart addCartItem(Product item, String cartKey, String username) {
        Cart cart = resolveOrCreateCart(cartKey, username);

        boolean found = false;
        for (var ci : cart.getCartItems()) {
            if (Objects.equals(ci.getProduct().getId(), item.getId())) {
                ci.setQuantity(ci.getQuantity() + 1);
                ci.setTotal(ci.getQuantity() * ci.getPrice());
                ci.setPrice(item.getPrice());
                found = true;
            }
        }

        if (!found) {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(item);
            cartItem.setQuantity(1);
            cartItem.setPrice(item.getPrice());
            cartItem.setTotal(item.getPrice() * cartItem.getQuantity());

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
    public Cart removeCartItem(Long id, String cartKey, String username) {
        Cart cart = resolveOrCreateCart(cartKey, username);

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
    public Page<Cart> findAll(Pageable pr) {
        return cartRepository.findAll(Objects.requireNonNullElseGet(pr, () -> PageRequest.ofSize(20)));
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
    public Cart incrementCartItem(Long id, String cartKey, String username) {
        Cart cart = resolveOrCreateCart(cartKey, username);
        if (cart == null) {
            throw new RuntimeException("Cart not found");
        }

        for (var ci : cart.getCartItems()) {
            if (Objects.equals(ci.getId(), id)) {
                ci.setQuantity(ci.getQuantity() + 1);
                ci.setTotal(ci.getQuantity() * ci.getPrice());
            }
        }

        int total = 0;
        for (var it : cart.getCartItems()) {
            total += it.getQuantity() * it.getPrice();
        }
        cart.setTotal(total);
        return saveCart(cart);
    }

    @Override
    public Cart decrementCartItem(Long id, String cartKey, String username) {
        Cart cart = resolveOrCreateCart(cartKey, username);
        if (cart == null) {
            throw new RuntimeException("Cart not found");
        }

        for (var ci : cart.getCartItems()) {
            if (Objects.equals(ci.getId(), id)) {
                if (ci.getQuantity() > 1) {
                    ci.setQuantity(ci.getQuantity() - 1);
                    ci.setTotal(ci.getQuantity() * ci.getPrice());
                } else {
                    throw new RuntimeException("quantity cant be less than one");
                }
            }
        }

        int total = 0;
        for (var it : cart.getCartItems()) {
            total += it.getQuantity() * it.getPrice();
        }
        cart.setTotal(total);
        return saveCart(cart);
    }

    @Override
    public Cart resolveOrCreateCart(String cartKey, String username) {
        Cart cart;
        if (username != null && !username.isEmpty()) {
            cart = cartRepository.findByUserId(username);
            if (cart == null) {
                cart = new Cart();
                cart.setUserId(username);
                cart.setSessionId(cartKey);
                cart = cartRepository.save(cart);
            }
        } else {
            cart = cartRepository.getCartBySession(cartKey);
            if (cart == null) {
                cart = new Cart();
                cart.setSessionId(cartKey);
                cart = cartRepository.save(cart);
            }
        }
        return cart;
    }
}
