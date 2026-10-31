package com.alamin.ecommerce.frontend.client.api;

import com.alamin.ecommerce.frontend.dto.CartDto;
import com.alamin.ecommerce.frontend.dto.CartItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * {@link CartClient} backed by {@link RestTemplate} against the backend
 * {@code /api/cart} endpoints.
 */
@Component
@RequiredArgsConstructor
public class CartClientImpl implements CartClient {

    private final RestTemplate restTemplate;
    private final String backendBaseUrl;

    private static final ParameterizedTypeReference<List<CartItemDto>> ITEM_LIST =
            new ParameterizedTypeReference<>() {
            };

    @Override
    public CartDto getCart() {
        return restTemplate.getForObject(backendBaseUrl + "/api/cart", CartDto.class);
    }

    @Override
    public List<CartItemDto> getCartItems() {
        return restTemplate.exchange(backendBaseUrl + "/api/cart/items",
                org.springframework.http.HttpMethod.GET, null, ITEM_LIST).getBody();
    }

    @Override
    public CartDto addItem(String productId, int quantity) {
        var uri = org.springframework.web.util.UriComponentsBuilder
                .fromUriString(backendBaseUrl + "/api/cart/items")
                .queryParam("productId", productId)
                .queryParam("quantity", quantity)
                .build().toUri();
        return restTemplate.postForObject(uri, null, CartDto.class);
    }

    @Override
    public CartDto removeItem(Long itemId) {
        restTemplate.delete(backendBaseUrl + "/api/cart/items/{id}", itemId);
        return getCart();
    }

    @Override
    public CartDto incrementItem(Long itemId) {
        return restTemplate.postForObject(
                backendBaseUrl + "/api/cart/items/{id}/increment", null, CartDto.class, itemId);
    }

    @Override
    public CartDto decrementItem(Long itemId) {
        return restTemplate.postForObject(
                backendBaseUrl + "/api/cart/items/{id}/decrement", null, CartDto.class, itemId);
    }
}
