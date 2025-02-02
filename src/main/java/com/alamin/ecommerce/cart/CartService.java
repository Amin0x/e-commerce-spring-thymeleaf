
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
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserService userService;

    public Optional<Cart> getCartById(Long id) {
        return cartRepository.findById(id);
    }

    public List<Cart> getCartByUserId(String userId){        
        return cartRepository.findByUserId(userId);
    }

    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }

    public void deleteCart(Long id) {
        cartRepository.deleteById(id);
    }

    public Cart addItemToCart(Product item, HttpSession session, Principal principal) {
        Cart cart = null;
        if (principal != null) {
            cart = cartRepository.findByUserIdAndProductId(principal.getName(), item.getProductId());
        } else {
            cart = null;
        }

        if(cart == null){
            cart = new Cart();
            cart.setUserId(principal.getName());
            cart.setProductId(item.getProductId());

        }else{

        }

        return saveCart(cart);
    }

    // Update the quantity of a product in the cart can be negitive to remove the product
    public Cart removeCartItem(Long itemId, HttpSession session, Principal principal) {
        User user = null;
        Cart cart = null;

        if (principal != null){
            user = userService.findByName(principal.getName()).orElseThrow();
        }

        if (user == null)
            cart = cartRepository.findByUserIdAndProductId(session.getId(), itemId);
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
}
