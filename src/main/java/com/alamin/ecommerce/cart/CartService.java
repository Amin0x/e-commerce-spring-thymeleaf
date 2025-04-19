package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.product.Product;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.util.Optional;

public interface CartService {
    Optional<Cart> getCartById(Long id);

    Cart getCartBySession(HttpSession session);

    Cart saveCart(Cart cart);

    void deleteCart(Long id);

    Cart addCartItem(Product item, HttpSession session, Principal principal);

    // Update the quantity of a product in the cart can be negative to remove the product
    Cart removeCartItem(Long id, HttpSession session, Principal principal);

    Page<Cart> findAll(Pageable pr);

    boolean existsById(Long id);

    Cart incrementCartItem(Long id, HttpSession session, Principal principal);

    Cart decrementCartItem(Long id, HttpSession session, Principal principal);
}
