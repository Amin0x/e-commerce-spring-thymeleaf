package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.product.Product;
import jakarta.servlet.http.HttpSession;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface CartService {
    Optional<Cart> getCartById(Long id);

    Cart getCartBySession(HttpSession session);

    Cart saveCart(Cart cart);

    void deleteCart(Long id);

    Cart addCartItem(Product item, HttpSession session, Principal principal);

    // Update the quantity of a product in the cart can be negative to remove the product
    Cart removeCartItem(Long id, HttpSession session, Principal principal);

    List<Cart> findAll();

    boolean existsById(Long id);

    Cart incrementCartItem(Long id, HttpSession session, Principal principal);

    Cart decrementCartItem(Long id, HttpSession session, Principal principal);
}
