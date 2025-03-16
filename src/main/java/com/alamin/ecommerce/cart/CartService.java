
package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.product.Product;
import com.alamin.ecommerce.user.User;
import com.alamin.ecommerce.user.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserService userService;

    public Optional<Cart> getCartById(Long id) {
        return cartRepository.findById(id);
    }

    public Cart getCartBySession(HttpSession session) {
        if (session == null)
            throw new IllegalArgumentException("no user found");

        return cartRepository.getCartBySession(session.getId());
    }

    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }

    public void deleteCart(Long id) {
        cartRepository.deleteById(id);
    }

    public Cart addItemToCart(Product item, HttpSession session, Principal principal) {
        Cart cart = null;
        if (principal != null && !principal.getName().isEmpty()) {
            cart = cartRepository.findByUserId(principal.getName());
        } else {
            cart = cartRepository.getCartBySession(session.getId());
        }

        if (cart == null) {
            cart = new Cart();
            cart.setUserId(principal == null ? null : principal.getName());
            cart.setSessionId(session.getId());
            cart = cartRepository.save(cart);
        }


        boolean found = false;
        for (var ci : cart.getCartItems()) {
            if (Objects.equals(ci.getProduct().getProductId(), item.getProductId())) {
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

        //update total
        int total = 0;
        for (var it:cart.getCartItems()){
            total += it.getQuantity() * it.getPrice();
        }
        cart.setTotal(total);
        Cart savedCart = saveCart(cart);
        return savedCart;
    }

    // Update the quantity of a product in the cart can be negitive to remove the product
    public Cart removeCartItem(Long itemId, HttpSession session, Principal principal) {
        User user = null;
        Cart cart = null;

        if (principal != null) {
            user = userService.getUserByName(principal.getName()).orElseThrow();
        }

        if (user == null)
            cart = cartRepository.getCartBySession(session.getId());
        else
            cart = null;


        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems == null)
            cartItems = new ArrayList<>();


        return saveCart(cart);
    }


    public List<Cart> findAll() {
        return cartRepository.findAll();
    }

    public boolean existsById(Long id) {
        return cartRepository.existsById(id);
    }

    public void incrementCartItem(Long id, HttpSession session, Principal principal) {
        Cart cart = getCartBySession(session);
        if (cart == null) {
            return;
        }

        for (var ci : cart.getCartItems()) {
            if (Objects.equals(ci.getId(), id)) {
                ci.setQuantity(ci.getQuantity() + 1);
                ci.setTotal(ci.getQuantity() * ci.getPrice());
            }
        }

        //update total
        int total = 0;
        for (var it:cart.getCartItems()){
            total += it.getQuantity() * it.getPrice();
        }
        cart.setTotal(total);
        saveCart(cart);
    }

    public void decrementCartItem(Long id, HttpSession session, Principal principal) {
        Cart cart = getCartBySession(session);
        if (cart == null) {
            return;
        }

        for (var ci : cart.getCartItems()) {
            if (Objects.equals(ci.getId(), id)) {
                ci.setQuantity(ci.getQuantity() - 1);
                ci.setTotal(ci.getQuantity() * ci.getPrice());
            }
        }

        //update total
        int total = 0;
        for (var it:cart.getCartItems()){
            total += it.getQuantity() * it.getPrice();
        }
        cart.setTotal(total);
        saveCart(cart);
    }
}
