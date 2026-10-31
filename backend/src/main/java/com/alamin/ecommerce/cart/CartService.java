package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.product.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CartService {
    Optional<Cart> getCartById(Long id);

    Cart getCartBySession(String cartKey);

    Cart resolveOrCreateCart(String cartKey, String username);

    Cart saveCart(Cart cart);

    void deleteCart(Long id);

    Cart addCartItem(ProductDto item, String cartKey, String username);

    Cart removeCartItem(Long id, String cartKey, String username);

    Page<Cart> findAll(Pageable pr);

    boolean existsById(Long id);

    Cart incrementCartItem(Long id, String cartKey, String username);

    Cart decrementCartItem(Long id, String cartKey, String username);
}
