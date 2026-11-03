package com.alamin.ecommerce.product;

import com.alamin.ecommerce.exception.ProductNotFoundException;
import com.alamin.ecommerce.config.JwtService;
import com.alamin.ecommerce.product.ProductReview;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void listProducts_defaultParameters_returnsOkAndContent() throws Exception {
        ProductDto product1 = new ProductDto(
                1L, "uuid-1", "Product 1", "Description 1", 100, 120, 100, "SKU-1", "slug-1",
                10, 5, 100, true, true, "image1.jpg", List.of(), 1L, "Electronics", true, 10.0);
        ProductDto product2 = new ProductDto(
                2L, "uuid-2", "Product 2", "Description 2", 200, 220, 200, "SKU-2", "slug-2",
                20, 3, 50, true, true, "image2.jpg", List.of(), 1L, "Electronics", false, 5.0);

        Page<ProductDto> page = new PageImpl<>(List.of(product1, product2), PageRequest.of(0, 12), 2);
        when(productService.getAllProducts(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].productId").value(1))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[1].productId").value(2))
                .andExpect(jsonPath("$[1].name").value("Product 2"));
    }

    @Test
    void listProducts_customPageSize_returnsPaginatedResults() throws Exception {
        ProductDto product = new ProductDto(
                1L, "uuid-1", "Product 1", "Description", 100, 120, 100, "SKU-1", "slug-1",
                10, 5, 100, true, true, "image1.jpg", List.of(), 1L, "Electronics", true, 10.0);

        Page<ProductDto> page = new PageImpl<>(List.of(product), PageRequest.of(1, 5), 10);
        when(productService.getAllProducts(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/products")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].productId").value(1));
    }

    @Test
    void listProducts_customSort_returnsSortedResults() throws Exception {
        ProductDto product1 = new ProductDto(
                1L, "uuid-1", "Product A", "Description", 100, 120, 100, "SKU-1", "slug-a",
                10, 5, 100, true, true, "image1.jpg", List.of(), 1L, "Electronics", true, 10.0);
        ProductDto product2 = new ProductDto(
                2L, "uuid-2", "Product B", "Description", 200, 220, 200, "SKU-2", "slug-b",
                20, 3, 50, true, true, "image2.jpg", List.of(), 1L, "Electronics", false, 5.0);

        Page<ProductDto> page = new PageImpl<>(List.of(product2, product1), PageRequest.of(0, 12, Sort.by("name").descending()), 2);
        when(productService.getAllProducts(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/products")
                        .param("sortBy", "name")
                        .param("asc", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Product B"))
                .andExpect(jsonPath("$[1].name").value("Product A"));
    }

    @Test
    void listProducts_emptyResult_returnsEmptyArray() throws Exception {
        Page<ProductDto> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 12), 0);
        when(productService.getAllProducts(any(PageRequest.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getProductById_exists_returnsProduct() throws Exception {
        ProductDto product = new ProductDto(
                1L, "uuid-1", "Product 1", "Description 1", 100, 120, 100, "SKU-1", "slug-1",
                10, 5, 100, true, true, "image1.jpg", List.of(), 1L, "Electronics", true, 10.0);

        when(productService.getProductById(1L)).thenReturn(java.util.Optional.of(product));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.name").value("Product 1"))
                .andExpect(jsonPath("$.uuid").value("uuid-1"))
                .andExpect(jsonPath("$.price").value(100));
    }

    @Test
    void getProductById_notExists_returnsNotFound() throws Exception {
        when(productService.getProductById(999L)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_validRequest_returnsCreatedProduct() throws Exception {
        ProductRequest request = new ProductRequest(
                "New Product", "Description", 100, 120, 90, "SKU-001", 50, 0, 0,
                true, true, 1L, List.of());

        ProductDto created = new ProductDto(
                1L, "uuid-new", "New Product", "Description", 100, 120, 90, "SKU-001", "new-product",
                50, 0, 0, true, true, null, List.of(), 1L, "Electronics", true, null);

        when(productService.save(any(ProductRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "New Product",
                                "description": "Description",
                                "price": 100,
                                "basePrice": 120,
                                "priceUSD": 90,
                                "sku": "SKU-001",
                                "stock": 50,
                                "totalSold": 0,
                                "viewCount": 0,
                                "active": true,
                                "enabled": true,
                                "categoryId": 1,
                                "imageIds": []
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.name").value("New Product"))
                .andExpect(jsonPath("$.price").value(100))
                .andExpect(jsonPath("$.sku").value("SKU-001"));
    }

    @Test
    void createProduct_missingName_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "price": 100,
                                "stock": 10
                            }
                            """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProduct_negativePrice_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Test Product",
                                "price": -10,
                                "stock": 10
                            }
                            """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadImage_validFile_returnsOk() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());

        when(productService.uploadImage(any(), eq(1L))).thenReturn(List.of());

        mockMvc.perform(multipart("/api/products/1/image")
                        .file(file))
                .andExpect(status().isOk());
    }

    @Test
    void uploadImage_productNotFound_returnsInternalServerError() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());

        when(productService.uploadImage(any(), eq(999L)))
                .thenThrow(new RuntimeException("Product not found"));

        mockMvc.perform(multipart("/api/products/999/image")
                        .file(file))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void uploadImage_missingFile_returnsInternalServerError() throws Exception {
        mockMvc.perform(multipart("/api/products/1/image"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void uploadImage_emptyFile_returnsOk() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[0]);

        when(productService.uploadImage(any(), eq(1L))).thenReturn(List.of());

        mockMvc.perform(multipart("/api/products/1/image")
                        .file(emptyFile))
                .andExpect(status().isOk());
    }

    @Test
    void updateProduct_validRequest_returnsUpdatedProduct() throws Exception {
        ProductDto updated = new ProductDto(
                1L, "uuid-1", "Updated Product", "Updated Description", 150, 180, 140, "SKU-001", "updated-product",
                60, 10, 200, true, true, "image1.jpg", List.of(), 1L, "Electronics", false, 5.0);

        when(productService.updateProduct(eq(1L), any(ProductRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Updated Product",
                                "description": "Updated Description",
                                "price": 150,
                                "basePrice": 180,
                                "priceUSD": 140,
                                "sku": "SKU-001",
                                "stock": 60,
                                "totalSold": 10,
                                "viewCount": 200,
                                "active": true,
                                "enabled": true,
                                "categoryId": 1,
                                "imageIds": []
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.price").value(150))
                .andExpect(jsonPath("$.sku").value("SKU-001"));
    }

    @Test
    void updateProduct_notFound_returnsBadRequest() throws Exception {
        when(productService.updateProduct(eq(999L), any(ProductRequest.class)))
                .thenThrow(new IllegalArgumentException("Product with ID 999 not found"));

        mockMvc.perform(put("/api/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Test",
                                "price": 100,
                                "stock": 10
                            }
                            """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProduct_invalidRequest_returnsBadRequest() throws Exception {
        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "",
                                "price": -5,
                                "stock": 10
                            }
                            """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteProduct_exists_returnsNoContent() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProduct_notFound_returnsBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("Product with ID 999 not found"))
                .when(productService).deleteProduct(999L);

        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProductReviews_exists_returnsReviews() throws Exception {
        ProductReview review1 = new ProductReview();
        review1.setId(1L);
        review1.setReview("Great product!");
        review1.setStar(5);
        review1.setReviewType("VERIFIED_PURCHASE");
        review1.setUserId(1L);
        review1.setProductId(1L);
        review1.setCreatedAt(java.time.LocalDateTime.now());
        review1.setUpdatedAt(java.time.LocalDateTime.now());

        ProductReview review2 = new ProductReview();
        review2.setId(2L);
        review2.setReview("Good value");
        review2.setStar(4);
        review2.setReviewType("VERIFIED_PURCHASE");
        review2.setUserId(2L);
        review2.setProductId(1L);
        review2.setCreatedAt(java.time.LocalDateTime.now());
        review2.setUpdatedAt(java.time.LocalDateTime.now());

        when(productService.getProductReviews(1L)).thenReturn(List.of(review1, review2));

        mockMvc.perform(get("/api/products/1/reviews"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].review").value("Great product!"))
                .andExpect(jsonPath("$[0].star").value(5))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].review").value("Good value"))
                .andExpect(jsonPath("$[1].star").value(4));
    }

    @Test
    void getProductReviews_noReviews_returnsEmptyArray() throws Exception {
        when(productService.getProductReviews(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/products/1/reviews"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}