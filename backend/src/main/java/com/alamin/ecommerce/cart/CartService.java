package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.product.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CartService {
    Optional<CartDto> getCartById(Long id);

    CartDto getCartBySession(String cartKey);

    CartDto getCartByUserId(String userId);

    CartDto saveCart(Cart cart);

    void deleteCart(Long id);

    CartDto addCartItem(ProductDto item, String cartKey, String username);

    CartDto removeCartItem(Long id, String cartKey, String username);

    Page<CartDto> findAll(Pageable pr);

    boolean existsById(Long id);

    CartDto updateCartItemQuantity(Long id, String cartKey, String username, int quantity);

    CartDto getOrCreateCart(String cartKey, String username);
}
