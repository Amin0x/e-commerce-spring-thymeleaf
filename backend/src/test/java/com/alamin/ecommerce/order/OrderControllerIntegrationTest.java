package com.alamin.ecommerce.order;

import com.alamin.ecommerce.config.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void getAllOrders_defaultParameters_returnsOkAndContent() throws Exception {
        Order order1 = createOrder(1L, "uuid-1", new BigDecimal("100.00"), OrderStatus.PENDING);
        Order order2 = createOrder(2L, "uuid-2", new BigDecimal("200.00"), OrderStatus.PROCESSING);

        Page<Order> page = new PageImpl<>(List.of(order1, order2), PageRequest.of(0, 10), 2);
        when(orderService.getAllOrders(eq(1), eq(10), eq(0), eq(0))).thenReturn(page);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].uuid").value("uuid-1"))
                .andExpect(jsonPath("$.content[0].totalAmount").value(100.00))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].uuid").value("uuid-2"))
                .andExpect(jsonPath("$.content[1].totalAmount").value(200.00))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void getAllOrders_customPageSize_returnsPaginatedResults() throws Exception {
        Order order = createOrder(1L, "uuid-1", new BigDecimal("150.00"), OrderStatus.PENDING);

        Page<Order> page = new PageImpl<>(List.of(order), PageRequest.of(2, 5), 15);
        when(orderService.getAllOrders(eq(3), eq(5), eq(0), eq(0))).thenReturn(page);

        mockMvc.perform(get("/api/orders")
                        .param("p", "3")
                        .param("s", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.number").value(2))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(15));
    }

    @Test
    void getAllOrders_customOrderAndSort_returnsSortedResults() throws Exception {
        Order order1 = createOrder(1L, "uuid-1", new BigDecimal("100.00"), OrderStatus.PENDING);
        Order order2 = createOrder(2L, "uuid-2", new BigDecimal("200.00"), OrderStatus.PROCESSING);

        Page<Order> page = new PageImpl<>(List.of(order2, order1), PageRequest.of(0, 10), 2);
        when(orderService.getAllOrders(eq(1), eq(10), eq(1), eq(1))).thenReturn(page);

        mockMvc.perform(get("/api/orders")
                        .param("p", "1")
                        .param("s", "10")
                        .param("o", "1")
                        .param("a", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.content[1].id").value(1));
    }

    @Test
    void getAllOrders_emptyResult_returnsEmptyContent() throws Exception {
        Page<Order> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(orderService.getAllOrders(eq(1), eq(10), eq(0), eq(0))).thenReturn(emptyPage);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void getOrderById_exists_returnsOrder() throws Exception {
        Order order = createOrder(1L, "uuid-1", new BigDecimal("100.00"), OrderStatus.PENDING);

        when(orderService.getOrderById(1L)).thenReturn(java.util.Optional.of(order));

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.uuid").value("uuid-1"))
                .andExpect(jsonPath("$.totalAmount").value(100.00))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getOrderById_notExists_returnsNotFound() throws Exception {
        when(orderService.getOrderById(999L)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound());
    }

    private Order createOrder(Long id, String uuid, BigDecimal totalAmount, OrderStatus status) {
        Order order = new Order();
        order.setId(id);
        order.setUuid(uuid);
        order.setTotalAmount(totalAmount);
        order.setStatus(status);
        order.setOrderDate(LocalDateTime.now());
        order.setPaymentMethod("CARD");
        order.setTransactionId("txn-" + id);
        order.setShippingFirstName("John");
        order.setShippingLastName("Doe");
        order.setShippingStreet("123 Main St");
        order.setShippingCity("New York");
        order.setShippingState("NY");
        order.setShippingCountry("USA");
        order.setShippingPostalCode("10001");
        order.setShippingPhone("555-1234");
        order.setPaymentStatus("PAID");
        return order;
    }
}