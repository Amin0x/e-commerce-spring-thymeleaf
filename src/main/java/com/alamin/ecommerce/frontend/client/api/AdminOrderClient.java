package com.alamin.ecommerce.frontend.client.api;

import com.alamin.ecommerce.frontend.dto.PageDto;
import com.alamin.ecommerce.frontend.dto.OrderDto;

/**
 * Admin client for order management on the backend {@code /api/admin/orders}
 * endpoints.
 */
public interface AdminOrderClient {

    PageDto<OrderDto> list(int page, int size, int order, Integer sort);

    OrderDto get(Long id);

    OrderDto update(Long id, OrderDto order);

    void delete(Long id);

    Long count();
}
