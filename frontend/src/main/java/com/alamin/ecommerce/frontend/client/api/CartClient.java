package com.alamin.ecommerce.frontend.client.api;

import com.alamin.ecommerce.frontend.dto.CartDto;
import com.alamin.ecommerce.frontend.dto.CartItemDto;

import java.util.List;

/**
 * Client for the backend cart API ({@code /api/cart}).
 *
 * <p>Cart identity is supplied by the {@link
 * com.alamin.ecommerce.frontend.client.interceptor.ClientAuthInterceptor} via the
 * {@code X-Cart-Key} header (guests) and the JWT principal (authenticated users);
 * this client only carries the product id / quantity parameters.
 */
public interface CartClient {

    CartDto getCart();

    List<CartItemDto> getCartItems();

    CartDto addItem(String productId, int quantity);

    CartDto removeItem(Long itemId);

    CartDto updateItemQuantity(Long itemId, int quantity);
}
