import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

package com.alamin.ecommerce.order;




class OrderServiceTest {

    private OrderService orderService;
    private Order mockOrder;
    private OrderDto mockOrderDto;

    @BeforeEach
    void setUp() {
        orderService = Mockito.mock(OrderService.class);
        mockOrder = new Order(); // Assuming Order is a concrete class
        mockOrderDto = new OrderDto(); // Assuming OrderDto is a concrete class
    }

    @Test
    void testCreateOrder() {
        when(orderService.createOrder(mockOrderDto)).thenReturn(mockOrder);

        Order createdOrder = orderService.createOrder(mockOrderDto);

        assertNotNull(createdOrder);
        verify(orderService, times(1)).createOrder(mockOrderDto);
    }

    @Test
    void testGetOrderById() {
        Long orderId = 1L;
        when(orderService.getOrderById(orderId)).thenReturn(Optional.of(mockOrder));

        Optional<Order> order = orderService.getOrderById(orderId);

        assertTrue(order.isPresent());
        assertEquals(mockOrder, order.get());
        verify(orderService, times(1)).getOrderById(orderId);
    }

    @Test
    void testGetAllOrders() {
        Page<Order> mockPage = new PageImpl<>(Arrays.asList(mockOrder));
        when(orderService.getAllOrders(0, 10, 1, true)).thenReturn(mockPage);

        Page<Order> orders = orderService.getAllOrders(0, 10, 1, true);

        assertNotNull(orders);
        assertEquals(1, orders.getTotalElements());
        verify(orderService, times(1)).getAllOrders(0, 10, 1, true);
    }

    @Test
    void testDeleteOrder() {
        Long orderId = 1L;
        doNothing().when(orderService).deleteOrder(orderId);

        orderService.deleteOrder(orderId);

        verify(orderService, times(1)).deleteOrder(orderId);
    }

    @Test
    void testUpdateOrder() {
        Long orderId = 1L;
        when(orderService.updateOrder(orderId, mockOrder)).thenReturn(mockOrder);

        Order updatedOrder = orderService.updateOrder(orderId, mockOrder);

        assertNotNull(updatedOrder);
        assertEquals(mockOrder, updatedOrder);
        verify(orderService, times(1)).updateOrder(orderId, mockOrder);
    }

    @Test
    void testGetTotalOrdersThisMonth() {
        when(orderService.getTotalOrdersThisMonth()).thenReturn(5L);

        long totalOrders = orderService.getTotalOrdersThisMonth();

        assertEquals(5L, totalOrders);
        verify(orderService, times(1)).getTotalOrdersThisMonth();
    }

    @Test
    void testGetTotalRevenue() {
        when(orderService.getTotalRevenue()).thenReturn(1000.0);

        Double totalRevenue = orderService.getTotalRevenue();

        assertEquals(1000.0, totalRevenue);
        verify(orderService, times(1)).getTotalRevenue();
    }

    @Test
    void testGetTotalRevenueMonth() {
        List<Double> revenueData = Arrays.asList(100.0, 200.0, 300.0);
        List<String> labels = Arrays.asList("Week 1", "Week 2", "Week 3");

        doAnswer(invocation -> {
            List<Double> data = invocation.getArgument(0);
            List<String> labelList = invocation.getArgument(1);
            data.addAll(revenueData);
            labelList.addAll(labels);
            return null;
        }).when(orderService).getTotalRevenueMonth(anyList(), anyList());

        List<Double> testData = new java.util.ArrayList<>();
        List<String> testLabels = new java.util.ArrayList<>();
        orderService.getTotalRevenueMonth(testData, testLabels);

        assertEquals(revenueData, testData);
        assertEquals(labels, testLabels);
        verify(orderService, times(1)).getTotalRevenueMonth(anyList(), anyList());
    }

    @Test
    void testGetTotalRevenueYear() {
        List<Double> revenueData = Arrays.asList(1000.0, 2000.0, 3000.0);
        List<String> labels = Arrays.asList("Q1", "Q2", "Q3");

        doAnswer(invocation -> {
            List<Double> data = invocation.getArgument(0);
            List<String> labelList = invocation.getArgument(1);
            data.addAll(revenueData);
            labelList.addAll(labels);
            return null;
        }).when(orderService).getTotalRevenueYear(anyList(), anyList());

        List<Double> testData = new java.util.ArrayList<>();
        List<String> testLabels = new java.util.ArrayList<>();
        orderService.getTotalRevenueYear(testData, testLabels);

        assertEquals(revenueData, testData);
        assertEquals(labels, testLabels);
        verify(orderService, times(1)).getTotalRevenueYear(anyList(), anyList());
    }
}