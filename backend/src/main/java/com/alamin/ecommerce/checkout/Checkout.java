package com.alamin.ecommerce.checkout;

import com.alamin.ecommerce.order.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class Checkout {

    @Autowired
    private OrderService orderService;

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/checkout")
    public void checkout(@RequestBody OrderDto orderDto){
        //if not authenticated redirect to auth link

        //create order with not complete status
        Order saveOrder = orderService.createOrder(orderDto);

        ResponseEntity<String> response = restTemplate.postForEntity("url", null, String.class);
        //redirect to payment gateway
    }
}
