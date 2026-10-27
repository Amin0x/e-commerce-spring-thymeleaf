package com.alamin.ecommerce.subscribe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SubscribeControllerTest {

    private final SubscribeService subscribeService;

    SubscribeControllerTest(SubscribeService subscribeService) {
        this.subscribeService = subscribeService;
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void subscribe() {
        String email = "test@test";
        String name = "test name";
        Subscribe subscribe = subscribeService.subscribe(email, name);

    }

    @Test
    void unsubscribe() {
    }

    @Test
    void verifyEmail() {
    }
}