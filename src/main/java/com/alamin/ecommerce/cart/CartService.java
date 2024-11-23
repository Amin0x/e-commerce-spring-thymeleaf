
package com.alamin.ecommerce.cart;

import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    public Cart getCart(Long id) {
        return cartRepository.findById(id).orElse(new Cart());
    }

    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }

    public void deleteCart(Long id) {
        cartRepository.deleteById(id);
    }

    public Cart addItemToCart(Long cartId, CartItem item) {
        Cart cart = getCart(cartId);
        boolean isExisting = false ;

        for (CartItem cartitem : cart.getItems()) {
            if (cartitem.getProductId().equals(item.getProductId())) {
                is Existing = true;
                long quan = cartitem.getQuantity() + item.getQuantity();
                cartitem.setQuantity(quan);
                break;
            }
        }

        if(!isExisting)
            cart.getItems().add(item);
        
        updateCartTotals(cart);
        return saveCart(cart);
    }

    public Cart removeItemFromCart(Long cartId, Long itemId) {
        Cart cart = getCart(cartId);
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        updateCartTotals(cart);
        return saveCart(cart);
    }

    private void updateCartTotals(Cart cart) {
        int totalQuantity = cart.getItems().stream().mapToInt(CartItem::getQuantity).sum();
        double totalPrice = cart.getItems().stream().mapToDouble(item -> item.getQuantity() * item.getPrice()).sum();
        cart.setTotalQuantity(totalQuantity);
        cart.setTotalPrice(totalPrice);
    }
}
