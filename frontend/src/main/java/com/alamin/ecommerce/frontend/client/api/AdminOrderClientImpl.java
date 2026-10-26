package com.alamin.ecommerce.frontend.client.api;

import com.alamin.ecommerce.frontend.dto.PageDto;
import com.alamin.ecommerce.frontend.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * {@link AdminOrderClient} backed by {@link RestTemplate} against the backend
 * {@code /api/admin/orders} endpoints.
 */
@Component
@RequiredArgsConstructor
public class AdminOrderClientImpl implements AdminOrderClient {

    private static final ParameterizedTypeReference<PageDto<OrderDto>> ORDER_PAGE =
            new ParameterizedTypeReference<>() {
            };

    private final RestTemplate restTemplate;
    private final String backendBaseUrl;

    private static final String ORDERS_URL = "/api/admin/orders";

    @Override
    public PageDto<OrderDto> list(int page, int size, int order, Integer sort) {
        var uri = UriComponentsBuilder.fromHttpUrl(backendBaseUrl + ORDERS_URL)
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("order", order)
                .queryParam("sort", sort == null ? 0 : sort)
                .build().toUri();
        return restTemplate.exchange(uri, HttpMethod.GET, null, ORDER_PAGE).getBody();
    }

    @Override
    public OrderDto get(Long id) {
        return restTemplate.getForObject(backendBaseUrl + ORDERS_URL + "/{id}", OrderDto.class, id);
    }

    @Override
    public OrderDto update(Long id, OrderDto order) {
        return restTemplate.exchange(
                backendBaseUrl + ORDERS_URL + "/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(order),
                OrderDto.class,
                id).getBody();
    }

    @Override
    public void delete(Long id) {
        restTemplate.delete(backendBaseUrl + ORDERS_URL + "/{id}", id);
    }

    @Override
    public Long count() {
        return restTemplate.getForObject(backendBaseUrl + ORDERS_URL + "/count", Long.class);
    }
}
