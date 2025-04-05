package com.alamin.ecommerce.product;

import com.alamin.ecommerce.cart.Cart;
import com.alamin.ecommerce.category.Category;
import com.alamin.ecommerce.category.CategoryService;
import com.alamin.ecommerce.config.FileUploadService;
import com.alamin.ecommerce.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductImageRepository productImageRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private ProductReviewRepository productReviewRepository;

    @Mock
    private ProductPriceRepository productPriceRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetRandomProductsByCategory() {
        Long categoryId = 1L;
        int size = 5;
        List<Product> mockProducts = List.of(new Product(), new Product());
        when(productRepository.findRandomProductsByCategory(categoryId, size)).thenReturn(mockProducts);

        List<Product> result = productService.getRandomProductsByCategory(categoryId, size);

        assertEquals(mockProducts, result);
        verify(productRepository, times(1)).findRandomProductsByCategory(categoryId, size);
    }

    @Test
    void testGetNewArrivalProducts() {
        List<Product> mockProducts = List.of(new Product(), new Product());
        when(productRepository.findNewArrivalProducts()).thenReturn(mockProducts);

        List<Product> result = productService.getNewArrivalProducts();

        assertEquals(mockProducts, result);
        verify(productRepository, times(1)).findNewArrivalProducts();
    }

    @Test
    void testGetAllProducts() {
        int page = 1;
        int size = 10;
        Page<Product> mockPage = new PageImpl<>(List.of(new Product(), new Product()));
        when(productRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        Page<Product> result = productService.getAllProducts(page, size);

        assertEquals(mockPage, result);
        verify(productRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testGetProductById() {
        Long productId = 1L;
        Product mockProduct = new Product();
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));

        Optional<Product> result = productService.getProductById(productId);

        assertTrue(result.isPresent());
        assertEquals(mockProduct, result.get());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testUpdateProduct() {
        Long productId = 1L;
        Product existingProduct = new Product();
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Name");
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);

        Product result = productService.updateProduct(productId, updatedProduct);

        assertEquals(existingProduct, result);
        assertEquals("Updated Name", existingProduct.getName());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(existingProduct);
    }

    @Test
    void testDeleteProduct() {
        Long productId = 1L;
        Product existingProduct = new Product();
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        productService.deleteProduct(productId);

        assertFalse(existingProduct.isActive());
        assertFalse(existingProduct.isEnabled());
        assertNotNull(existingProduct.getDeleted());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(existingProduct);
    }

    @Test
    void testUploadImage() {
        Long productId = 1L;
        MultipartFile file = mock(MultipartFile.class);
        Product product = new Product();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(fileUploadService.uploadFile(file)).thenReturn("uploaded-file.jpg");

        List<ProductImage> result = productService.uploadImage(file, productId);

        assertEquals(1, result.size());
        assertEquals("uploaded-file.jpg", result.get(0).getFileName());
        verify(productRepository, times(1)).findById(productId);
        verify(fileUploadService, times(1)).uploadFile(file);
    }

    @Test
    void testSaveProduct() {
        Product product = new Product();
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.save(product);

        assertEquals(product, result);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testFindByCategory() {
        String category = "Electronics";
        List<Product> mockProducts = List.of(new Product(), new Product());
        when(productRepository.findByCategory(category)).thenReturn(mockProducts);

        List<Product> result = productService.findByCategory(category);

        assertEquals(mockProducts, result);
        verify(productRepository, times(1)).findByCategory(category);
    }

    @Test
    void testExistsById() {
        Long productId = 1L;
        when(productRepository.existsById(productId)).thenReturn(true);

        boolean result = productService.existsById(productId);

        assertTrue(result);
        verify(productRepository, times(1)).existsById(productId);
    }

    @Test
    void testDeleteById() {
        Long productId = 1L;

        productService.deleteById(productId);

        verify(productRepository, times(1)).deleteById(productId);
    }
}