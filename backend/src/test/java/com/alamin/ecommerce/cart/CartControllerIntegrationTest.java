package com.alamin.ecommerce.cart;

import com.alamin.ecommerce.config.JwtService;
import com.alamin.ecommerce.product.Product;
import com.alamin.ecommerce.product.ProductDto;
import com.alamin.ecommerce.product.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CartControllerIntegrationTest.TestConfig.class)
class CartControllerIntegrationTest {

    @TestConfiguration
    static class TestConfig implements WebMvcConfigurer {
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new AuthenticationPrincipalArgumentResolver());
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void getCart_guestUser_returnsCart() throws Exception {
        CartDto cart = createCart(1L, "guest-key-123", null, 100);
        when(cartService.getOrCreateCart(eq("guest-key-123"), isNull())).thenReturn(cart);

        mockMvc.perform(get("/api/cart")
                        .header("X-Cart-Key", "guest-key-123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.uuid").value("guest-key-123"))
                .andExpect(jsonPath("$.sessionId").value("guest-key-123"))
                .andExpect(jsonPath("$.total").value(100))
                .andExpect(jsonPath("$.userId").isEmpty());
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void getCart_authenticatedUser_returnsCart() throws Exception {
        CartDto cart = createCart(1L, "user-key-456", "user@example.com", 200);
        when(cartService.getOrCreateCart(eq("user-key-456"), eq("user@example.com"))).thenReturn(cart);

        mockMvc.perform(get("/api/cart")
                        .header("X-Cart-Key", "user-key-456"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.sessionId").value("user-key-456"))
                .andExpect(jsonPath("$.userId").value("user@example.com"))
                .andExpect(jsonPath("$.total").value(200));
    }

    @Test
    void getCart_noCartKey_returnsCartWithNullKey() throws Exception {
        CartDto cart = createCart(1L, null, null, 0);
        when(cartService.getOrCreateCart(isNull(), isNull())).thenReturn(cart);

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.total").value(0));
    }

    @Test
    void getCartItems_guestUser_returnsCartItems() throws Exception {
        CartDto cart = createCartWithItems(1L, "guest-key-123", null);
        when(cartService.getOrCreateCart(eq("guest-key-123"), isNull())).thenReturn(cart);

        mockMvc.perform(get("/api/cart/items")
                        .header("X-Cart-Key", "guest-key-123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].quantity").value(2))
                .andExpect(jsonPath("$[0].product.name").value("Product 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].quantity").value(1))
                .andExpect(jsonPath("$[1].product.name").value("Product 2"));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void getCartItems_authenticatedUser_returnsCartItems() throws Exception {
        CartDto cart = createCartWithItems(1L, "user-key-456", "user@example.com");
        when(cartService.getOrCreateCart(eq("user-key-456"), eq("user@example.com"))).thenReturn(cart);

        mockMvc.perform(get("/api/cart/items")
                        .header("X-Cart-Key", "user-key-456"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getCartItems_emptyCart_returnsEmptyArray() throws Exception {
        CartDto cart = createCart(1L, "guest-key-123", null, 0);
        when(cartService.getOrCreateCart(eq("guest-key-123"), isNull())).thenReturn(cart);

        mockMvc.perform(get("/api/cart/items")
                        .header("X-Cart-Key", "guest-key-123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void addItem_validProduct_returnsUpdatedCart() throws Exception {
        String productId = "prod-uuid-123";
        ProductDto product = createProductDto(1L, productId, "Test Product", 100);
        CartDto cart = createCartWithItems(1L, "guest-key-123", null);

        when(productService.getProductByUuid(eq(productId))).thenReturn(Optional.of(product));
        when(cartService.addCartItem(any(), nullable(String.class), nullable(String.class))).thenReturn(cart);

        mockMvc.perform(post("/api/cart/items")
                        .header("X-Cart-Key", "guest-key-123")
                        .param("productId", productId)
                        .param("quantity", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.total").value(300));
    }

    @Test
    void addItem_defaultQuantity_usesQuantityOne() throws Exception {
        String productId = "prod-uuid-123";
        ProductDto product = createProductDto(1L, productId, "Test Product", 100);
        CartDto cart = createCart(1L, "guest-key-123", null, 100);

        when(productService.getProductByUuid(eq(productId))).thenReturn(Optional.of(product));
        when(cartService.addCartItem(any(), nullable(String.class), nullable(String.class))).thenReturn(cart);

        mockMvc.perform(post("/api/cart/items")
                        .header("X-Cart-Key", "guest-key-123")
                        .param("productId", productId))
                .andExpect(status().isOk());
    }

    @Test
    void addItem_productNotFound_returnsBadRequest() throws Exception {
        String productId = "non-existent-uuid";
        when(productService.getProductByUuid(eq(productId))).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/cart/items")
                        .header("X-Cart-Key", "guest-key-123")
                        .param("productId", productId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void addItem_authenticatedUser_returnsUpdatedCart() throws Exception {
        String productId = "prod-uuid-123";
        ProductDto product = createProductDto(1L, productId, "Test Product", 100);
        CartDto cart = createCartWithItems(1L, "user-key-456", "user@example.com");

        when(productService.getProductByUuid(eq(productId))).thenReturn(Optional.of(product));
        when(cartService.addCartItem(any(), nullable(String.class), nullable(String.class))).thenReturn(cart);

        mockMvc.perform(post("/api/cart/items")
                        .header("X-Cart-Key", "user-key-456")
                        .param("productId", productId)
                        .param("quantity", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user@example.com"));
    }

    @Test
    void removeItem_existingItem_returnsUpdatedCart() throws Exception {
        CartDto cart = createCart(1L, "guest-key-123", null, 100);
        when(cartService.removeCartItem(anyLong(), nullable(String.class), nullable(String.class))).thenReturn(cart);

        mockMvc.perform(delete("/api/cart/items/1")
                        .header("X-Cart-Key", "guest-key-123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.total").value(100));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void removeItem_authenticatedUser_returnsUpdatedCart() throws Exception {
        CartDto cart = createCart(1L, "user-key-456", "user@example.com", 50);
        when(cartService.removeCartItem(anyLong(), nullable(String.class), nullable(String.class))).thenReturn(cart);

        mockMvc.perform(delete("/api/cart/items/1")
                        .header("X-Cart-Key", "user-key-456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user@example.com"));
    }

    @Test
    void updateItemQuantity_existingItem_returnsUpdatedCart() throws Exception {
        CartDto cart = createCartWithItems(1L, "guest-key-123", null);
        when(cartService.updateCartItemQuantity(anyLong(), nullable(String.class), nullable(String.class), anyInt())).thenReturn(cart);

        mockMvc.perform(patch("/api/cart/items/1")
                        .header("X-Cart-Key", "guest-key-123")
                        .param("quantity", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cartItems.length()").value(2));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void updateItemQuantity_authenticatedUser_returnsUpdatedCart() throws Exception {
        CartDto cart = createCartWithItems(1L, "user-key-456", "user@example.com");
        when(cartService.updateCartItemQuantity(anyLong(), nullable(String.class), nullable(String.class), anyInt())).thenReturn(cart);

        mockMvc.perform(patch("/api/cart/items/1")
                        .header("X-Cart-Key", "user-key-456")
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user@example.com"));
    }

    @Test
    void updateItemQuantity_invalidQuantity_returnsBadRequest() throws Exception {
        when(cartService.updateCartItemQuantity(anyLong(), nullable(String.class), nullable(String.class), eq(0)))
                .thenThrow(new IllegalArgumentException("Quantity must be at least 1"));

        mockMvc.perform(patch("/api/cart/items/1")
                        .header("X-Cart-Key", "guest-key-123")
                        .param("quantity", "0"))
                .andExpect(status().isBadRequest());
    }

    private CartDto createCart(Long id, String uuid, String userId, int total) {
        return new CartDto(createCartEntity(id, uuid, userId, total));
    }

    private CartDto createCartWithItems(Long id, String uuid, String userId) {
        Cart cart = createCartEntity(id, uuid, userId, 300);

        CartItem item1 = new CartItem();
        item1.setId(1L);
        item1.setQuantity(2);
        item1.setPrice(100);
        item1.setTotal(200);
        item1.setProduct(createProduct(1L, "prod-1", "Product 1", 100));

        CartItem item2 = new CartItem();
        item2.setId(2L);
        item2.setQuantity(1);
        item2.setPrice(100);
        item2.setTotal(100);
        item2.setProduct(createProduct(2L, "prod-2", "Product 2", 100));

        cart.getCartItems().add(item1);
        cart.getCartItems().add(item2);
        item1.setCart(cart);
        item2.setCart(cart);

        return new CartDto(cart);
    }

    private Cart createCartEntity(Long id, String uuid, String userId, int total) {
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
        return cart;
    }

    private ProductDto createProductDto(Long id, String uuid, String name, int price) {
        return new ProductDto(
                id, uuid, name, "Description", price, price, price, "SKU-" + id, "slug-" + id,
                10, 5, 100, true, true, "image.jpg", List.of(), 1L, "Category", false, 0.0
        );
    }

    private Product createProduct(Long id, String uuid, String name, int price) {
        Product product = new Product();
        product.setId(id);
        product.setUuid(uuid);
        product.setName(name);
        product.setPrice(price);
        product.setBasePrice(price);
        product.setPriceUSD(price);
        product.setSku("SKU-" + id);
        product.setSlug("slug-" + id);
        product.setStock(10);
        product.setActive(true);
        product.setEnabled(true);
        return product;
    }
}