package com.alamin.ecommerce.order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    private OrderRepository orderRepository;
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderRepository = Mockito.mock(OrderRepository.class);
        orderService = new OrderServiceImpl();
        
    }

    @Test
    void testRoundToNearestMultiple() {
        assertEquals(100, OrderServiceImpl.roundToNearestMultiple(123));
        assertEquals(1000, OrderServiceImpl.roundToNearestMultiple(1500));
        assertEquals(10, OrderServiceImpl.roundToNearestMultiple(7));
    }

    @Test
    void testCreateOrder() {
        OrderDto orderDto = new OrderDto(            
            "Credit Card", // paymentMethod
            "1234567890123456", // cardNumber
            "123", // ccv
            "12/25", // cardDate
            "John Doe", // cardHolder
            "FedEx", // carrier
            "123 Main St", // street
            "New York", // city
            "NY", // state
            "USA", // country
            "10001", // postalCode
            "John", // firstName
            "Doe" // lastName
        );
        Order savedOrder = new Order();
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Order result = orderService.createOrder(orderDto);

        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testGetOrderById() {
        Order order = new Order();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Optional<Order> result = orderService.getOrderById(1L);

        assertTrue(result.isPresent());
        assertEquals(order, result.get());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllOrdersWithPaginationAndSorting() {
        Page<Order> orders = new PageImpl<>(Arrays.asList(new Order(), new Order()));
        when(orderRepository.findAll(any(PageRequest.class))).thenReturn(orders);

        Page<Order> result = orderService.getAllOrders(0, 10, 1, 0);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(orderRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void testDeleteOrder() {
        doNothing().when(orderRepository).deleteById(1L);

        orderService.deleteOrder(1L);

        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateOrder() {
        Order existingOrder = new Order();
        existingOrder.setOrderDate(LocalDateTime.now());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(existingOrder);

        Order updatedOrder = new Order();
        updatedOrder.setOrderDate(LocalDateTime.now().plusDays(1));
        Order result = orderService.updateOrder(1L, updatedOrder);

        assertNotNull(result);
        assertEquals(updatedOrder.getOrderDate(), result.getOrderDate());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testGetTotalOrdersThisMonth() {
        when(orderRepository.countOrdersByMonthAndProductId(anyInt(), anyInt())).thenReturn(5L);

        long result = orderService.getTotalOrdersThisMonth();

        assertEquals(5L, result);
        verify(orderRepository, times(1)).countOrdersByMonthAndProductId(anyInt(), anyInt());
    }

    @Test
    void testGetTotalRevenue() {
        when(orderRepository.getTotalRevenue()).thenReturn(1000.0);

        Double result = orderService.getTotalRevenue();

        assertEquals(1000.0, result);
        verify(orderRepository, times(1)).getTotalRevenue();
    }

    @Test
    void testGetTotalRevenueMonth() {
        when(orderRepository.getTotalRevenue(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(100.0);

        List<Double> listData = new ArrayList<>();
        List<String> listLabels = new ArrayList<>();
        orderService.getTotalRevenueMonth(listData, listLabels);

        assertFalse(listData.isEmpty());
        assertFalse(listLabels.isEmpty());
        verify(orderRepository, atLeastOnce()).getTotalRevenue(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testGetTotalRevenueYear() {
        when(orderRepository.getTotalRevenue(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(100.0);

        List<Double> listData = new ArrayList<>();
        List<String> listLabels = new ArrayList<>();
        orderService.getTotalRevenueYear(listData, listLabels);

        assertFalse(listData.isEmpty());
        assertFalse(listLabels.isEmpty());
        verify(orderRepository, atLeastOnce()).getTotalRevenue(any(LocalDateTime.class), any(LocalDateTime.class));
    }
}