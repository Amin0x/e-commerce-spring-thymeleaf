
package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.product.Product;
import com.alamin.ecommerce.user.User;
import com.alamin.ecommerce.user.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserServiceImpl userService;

    @Override
    public Optional<Cart> getCartById(Long id) {
        return cartRepository.findById(id);
    }

    @Override
    public Cart getCartBySession(HttpSession session) {
        if (session == null)
            throw new IllegalArgumentException("no user found");

        return cartRepository.getCartBySession(session.getId());
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
    public Cart addCartItem(Product item, HttpSession session, Principal principal) {
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
    @Override
    public Cart removeCartItem(Long id, HttpSession session, Principal principal) {
        User user = null;
        Cart cart = null;

        if (principal != null) {
            user = userService.getUserByName(principal.getName())
                .orElseThrow( () -> new RuntimeException("User not found"));

            cart = cartRepository.findByUserId(user.getUsername());
            if (cart == null) {
                cart = new Cart();
                cart.setUserId(user.getUsername());
                cart.setSessionId(session.getId());
                cart = cartRepository.save(cart);
            }
        } else {
            if (session == null) {
                throw new RuntimeException("Session not found");
            }
            cart = cartRepository.getCartBySession(session.getId());
        }


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
    public List<Cart> findAll() {
        return cartRepository.findAll();
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
    public Cart incrementCartItem(Long id, HttpSession session, Principal principal) {
        Cart cart = getCartBySession(session);
        if (cart == null) {
            throw new RuntimeException("Cart not found");
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
        return saveCart(cart);
    }

    @Override
    public Cart decrementCartItem(Long id, HttpSession session, Principal principal) {
        Cart cart = getCartBySession(session);
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

        //update total
        int total = 0;
        for (var it:cart.getCartItems()){
            total += it.getQuantity() * it.getPrice();
        }
        cart.setTotal(total);
        return saveCart(cart);
    }
}
