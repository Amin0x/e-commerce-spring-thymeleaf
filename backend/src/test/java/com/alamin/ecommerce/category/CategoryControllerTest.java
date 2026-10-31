package com.alamin.ecommerce.category;

import com.alamin.ecommerce.product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.NOT_FOUND;

class CategoryControllerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCategories() {
        List<Category> mockCategories = new ArrayList<>();
        mockCategories.add(new Category());
        mockCategories.add(new Category());
        when(categoryRepository.findAll()).thenReturn(mockCategories);

        // Act
        ResponseEntity<List<Category>> response = categoryController.list();

        // Assert
        assertEquals(OK.value(), response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void testGetCategoryById_CategoryExists() {
        Long categoryId = 1L;
        Category mockCategory = new Category();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));

        // Act
        ResponseEntity<Category> response = categoryController.getById(categoryId);

        // Assert
        assertEquals(OK.value(), response.getStatusCode().value());
        assertEquals(mockCategory, response.getBody());
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void testGetCategoryById_CategoryDoesNotExist() {
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Category> response = categoryController.getById(categoryId);

        // Assert
        assertEquals(NOT_FOUND.value(), response.getStatusCode().value());
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void testGetCategoryProducts_CategoryExists() {
        String categorySlug = "electronics";
        Category mockCategory = new Category();
        when(categoryRepository.findBySlug(categorySlug)).thenReturn(Optional.of(mockCategory));

        // Act
        ResponseEntity<Category> response = categoryController.getBySlug(categorySlug);

        // Assert
        assertEquals(OK.value(), response.getStatusCode().value());
        assertEquals(mockCategory, response.getBody());
        verify(categoryRepository, times(1)).findBySlug(categorySlug);
    }

    @Test
    void testGetCategoryProducts_CategoryDoesNotExist() {
        String categorySlug = "electronics";
        when(categoryRepository.findBySlug(categorySlug)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Category> response = categoryController.getBySlug(categorySlug);

        // Assert
        assertEquals(NOT_FOUND.value(), response.getStatusCode().value());
        verify(categoryRepository, times(1)).findBySlug(categorySlug);
    }
}
