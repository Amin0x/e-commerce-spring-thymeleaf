
package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

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

    public Cart addItemToCart(Product item, String userId) {

        Cart cart = cartRepository.findByUserIdAndProductId( userId, item.getProductId());
        if(cart == null){
            cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(item.getProductId());
            cart.setQuantity(1);
            cart.setPrice(item.getPrice());
            cart.setTotal(item.getPrice() * cart.getQuantity());
        }else{
            cart.setQuantity(cart.getQuantity() + 1);
            cart.setTotal(cart.getTotal() + (item.getPrice() * cart.getQuantity()));
        }

        return saveCart(cart);
    }

    // Update the quantity of a product in the cart can be negitive to remove the product
    public Cart removeCartItem(Long itemId, String userId) {
        Cart cart = cartRepository.findByUserIdAndProductId(userId, itemId);
        if (cart == null) {
            return null;
        }
        cart.setQuantity(cart.getQuantity() - 1);
        if (cart.getQuantity() <= 0) {
            deleteCartItem(itemId, userId);
            return null;
        }
        cart.setTotal(cart.getPrice() * cart.getQuantity());
        return saveCart(cart);
    }

    public void deleteCartItem(Long itemId, String userId) {
        cartRepository.deleteByUserIdAndProductId(itemId, userId);
    }

    public List<Cart> findAll() {
        return cartRepository.findAll();
    }

    public boolean existsById(Long id) {
        return cartRepository.existsById(id);
    }
}
