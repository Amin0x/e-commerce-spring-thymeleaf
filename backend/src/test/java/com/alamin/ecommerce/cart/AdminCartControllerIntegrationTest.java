package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.config.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminCartController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(AdminCartControllerIntegrationTest.TestConfig.class)
class AdminCartControllerIntegrationTest {

    @TestConfiguration
    static class TestConfig {
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void listAll_admin_returnsPaginatedCarts() throws Exception {
        CartDto cart1 = createCart(1L, "cart-1", "user1@example.com", 100);
        CartDto cart2 = createCart(2L, "cart-2", "user2@example.com", 200);
        Page<CartDto> page = new PageImpl<>(List.of(cart1, cart2), PageRequest.of(0, 10), 2);
        when(cartService.findAll(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/admin/cart")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].userId").value("user1@example.com"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].userId").value("user2@example.com"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void listAll_admin_emptyResult_returnsEmptyContent() throws Exception {
        Page<CartDto> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(cartService.findAll(any(PageRequest.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/api/admin/cart"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void listAll_admin_customPageSize_returnsPaginatedResults() throws Exception {
        CartDto cart = createCart(1L, "cart-1", "user1@example.com", 100);
        Page<CartDto> page = new PageImpl<>(List.of(cart), PageRequest.of(1, 5), 15);
        when(cartService.findAll(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/admin/cart")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(15));
    }

    private CartDto createCart(Long id, String uuid, String userId, int total) {
        Cart cart = new Cart();
        cart.setId(id);
        cart.setUuid(uuid);
        cart.setSessionId(uuid);
        cart.setUserId(userId);
        cart.setTotal(total);
        cart.setStatus("ACTIVE");
        cart.setCurrency("USD");
        cart.setCreated(LocalDateTime.now());
        cart.setUpdated(LocalDateTime.now());
        cart.setCartItems(new ArrayList<>());
        return new CartDto(cart);
    }
}