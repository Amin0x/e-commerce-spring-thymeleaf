package com.alamin.ecommerce.checkout;

import com.alamin.ecommerce.order.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;

@Controller
public class Checkout {

    @Autowired
    private OrderService orderService;

    @RequestMapping("/checkout")
    public void checkout(@RequestBody OrderDto orderDto){

        Order order = new Order();

        //order.setOrderItems();
        order.setCarrier("");
        order.setCardNumber("");
        order.setEstimatedArrival("");
        order.setPaymentMethod("");
        order.setPaymentStatus("");
        order.setShipping(BigDecimal.valueOf(0.0));
        order.setStatus(OrderStatus.PENDING);
        order.setTax(BigDecimal.valueOf(0));
        order.setTotalAmount(BigDecimal.valueOf(0));
        order.setTransactionId("");
        Address address = new Address();
        address.setState("khartoum");
        address.setCity("khartoum");
        address.setCountry("sudan");
        address.setPostalCode("111111");
        Customer customer = new Customer(orderDto.firstName(), orderDto.lastName(), address, null);
        customer.setEmail("test@test.com");
        order.setCustomer(customer);

        Order saveOrder = orderService.saveOrder(order);
    }
}
