/*
 * Copyright (c) 2024 Alamin Omer.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Class Name: CartService
 * Description: This class represents a cart service in the e-commerce application.
 * Author: Alamin
 * Date Created: 2024-11-23
 */
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
