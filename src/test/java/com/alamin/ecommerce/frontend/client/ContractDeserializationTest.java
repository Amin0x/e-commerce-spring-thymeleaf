package com.alamin.ecommerce.frontend.client;

import com.alamin.ecommerce.frontend.dto.AuthResponse;
import com.alamin.ecommerce.frontend.dto.CartDto;
import com.alamin.ecommerce.frontend.dto.CategoryDto;
import com.alamin.ecommerce.frontend.dto.ProductDto;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Contract deserialization test.
 *
 * <p>Validates that the frontend's mirrored DTOs can deserialize the JSON contract
 * fixtures (copied from {@code backend/src/test/resources/contracts}). These fixtures
 * are the source of truth for the backend↔frontend contract; if the backend changes
 * its JSON shape, these fixtures and the DTOs must be updated together.
 */
@SpringBootTest(classes = com.alamin.ecommerce.frontend.FrontendApplication.class)
class ContractDeserializationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    void productsContractDeserializes() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:contracts/products.json");
        List<ProductDto> products = objectMapper.readValue(
                resource.getInputStream(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, ProductDto.class));
        assertThat(products).hasSize(2);
        assertThat(products.get(0).getProductId()).isEqualTo(101L);
        assertThat(products.get(0).getName()).isEqualTo("Wireless Headphones");
        assertThat(products.get(0).getPrice()).isEqualTo(2500);
        assertThat(products.get(0).getBasePrice()).isEqualTo(3000);
        assertThat(products.get(0).getSlug()).isEqualTo("wireless-headphones");
        assertThat(products.get(0).getCategoryId()).isEqualTo(5L);
        assertThat(products.get(0).getProductImages()).hasSize(1);
        assertThat(products.get(1).getProductId()).isEqualTo(102L);
        assertThat(products.get(1).getImage()).isEqualTo("tshirt.jpg");
    }

    @Test
    void categoriesContractDeserializes() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:contracts/categories.json");
        List<CategoryDto> categories = objectMapper.readValue(
                resource.getInputStream(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, CategoryDto.class));
        assertThat(categories).hasSize(2);
        assertThat(categories.get(0).getId()).isEqualTo(5L);
        assertThat(categories.get(0).getName()).isEqualTo("Electronics");
        assertThat(categories.get(1).getId()).isEqualTo(7L);
        assertThat(categories.get(1).getName()).isEqualTo("Clothing");
    }

    @Test
    void cartContractDeserializes() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:contracts/cart.json");
        CartDto cart = objectMapper.readValue(resource.getInputStream(), CartDto.class);
        assertThat(cart.getId()).isEqualTo(2001L);
        assertThat(cart.getTotal()).isEqualTo(2950);
        assertThat(cart.getCartItems()).hasSize(2);
        assertThat(cart.getCartItems().get(0).getId()).isEqualTo(3001L);
        assertThat(cart.getCartItems().get(0).getQuantity()).isEqualTo(1);
        assertThat(cart.getCartItems().get(0).getProduct().getProductId()).isEqualTo(101L);
        assertThat(cart.getCartItems().get(0).getProduct().getName()).isEqualTo("Wireless Headphones");
        assertThat(cart.getCartItems().get(1).getProduct().getName()).isEqualTo("Cotton T-Shirt");
    }

    @Test
    void authResponseContractDeserializes() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:contracts/auth-response.json");
        AuthResponse auth = objectMapper.readValue(resource.getInputStream(), AuthResponse.class);
        assertThat(auth.getUsername()).isEqualTo("alice");
        assertThat(auth.getRole()).isEqualTo("USER");
        assertThat(auth.getUid()).isEqualTo("123456");
        assertThat(auth.getToken()).isNotBlank();
    }
}
