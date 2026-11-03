package com.alamin.ecommerce.category;

import com.alamin.ecommerce.config.JwtService;
import com.alamin.ecommerce.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void list_returnsAllCategories() throws Exception {
        Category cat1 = createCategory(1L, "uuid-1", "Electronics", "Electronics desc", "electronics", true, null);
        Category cat2 = createCategory(2L, "uuid-2", "Clothing", "Clothing desc", "clothing", true, cat1);
        when(categoryService.findAll()).thenReturn(List.of(cat1, cat2));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Electronics"))
                .andExpect(jsonPath("$[0].slug").value("electronics"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Clothing"))
                .andExpect(jsonPath("$[1].slug").value("clothing"));
    }

    @Test
    void list_emptyResult_returnsEmptyArray() throws Exception {
        when(categoryService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void top_returnsTopCategories() throws Exception {
        Category cat1 = createCategory(1L, "uuid-1", "Electronics", "Electronics desc", "electronics", true, null);
        Category cat2 = createCategory(2L, "uuid-2", "Clothing", "Clothing desc", "clothing", true, null);
        when(categoryService.getTopCategories()).thenReturn(List.of(cat1, cat2));

        mockMvc.perform(get("/api/categories/top"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Electronics"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Clothing"));
    }

    @Test
    void getById_exists_returnsCategory() throws Exception {
        Category cat = createCategory(1L, "uuid-1", "Electronics", "Electronics desc", "electronics", true, null);
        when(categoryService.getCategoryById(1L)).thenReturn(java.util.Optional.of(cat));

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Electronics"))
                .andExpect(jsonPath("$.slug").value("electronics"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void getById_notExists_returnsNotFound() throws Exception {
        when(categoryService.getCategoryById(999L)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/categories/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBySlug_exists_returnsCategory() throws Exception {
        Category cat = createCategory(1L, "uuid-1", "Electronics", "Electronics desc", "electronics", true, null);
        when(categoryService.findByCategorySlug("electronics")).thenReturn(java.util.Optional.of(cat));

        mockMvc.perform(get("/api/categories/slug/electronics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.slug").value("electronics"))
                .andExpect(jsonPath("$.name").value("Electronics"));
    }

    @Test
    void getBySlug_notExists_returnsNotFound() throws Exception {
        when(categoryService.findByCategorySlug("non-existent")).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/categories/slug/non-existent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void active_returnsFilteredCategories() throws Exception {
        Category cat1 = createCategory(1L, "uuid-1", "Electronics", "Electronics desc", "electronics", true, null);
        Category cat2 = createCategory(2L, "uuid-2", "Clothing", "Clothing desc", "clothing", false, null);
        when(categoryService.getCategoriesByActive(true)).thenReturn(List.of(cat1));
        when(categoryService.getCategoriesByActive(false)).thenReturn(List.of(cat2));

        mockMvc.perform(get("/api/categories/active")
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].active").value(true));

        mockMvc.perform(get("/api/categories/active")
                        .param("active", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].active").value(false));
    }

    @Test
    void active_defaultParameter_returnsActiveCategories() throws Exception {
        Category cat = createCategory(1L, "uuid-1", "Electronics", "Electronics desc", "electronics", true, null);
        when(categoryService.getCategoriesByActive(true)).thenReturn(List.of(cat));

        mockMvc.perform(get("/api/categories/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    void byParent_returnsSubcategories() throws Exception {
        Category parent = createCategory(1L, "uuid-1", "Electronics", "Electronics desc", "electronics", true, null);
        Category child1 = createCategory(2L, "uuid-2", "Phones", "Phones desc", "phones", true, parent);
        Category child2 = createCategory(3L, "uuid-3", "Laptops", "Laptops desc", "laptops", true, parent);
        when(categoryService.getSubcategoriesByParentId(1L)).thenReturn(List.of(child1, child2));

        mockMvc.perform(get("/api/categories/parent/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Phones"))
                .andExpect(jsonPath("$[1].id").value(3))
                .andExpect(jsonPath("$[1].name").value("Laptops"));
    }

    @Test
    void byParent_emptyResult_returnsEmptyArray() throws Exception {
        when(categoryService.getSubcategoriesByParentId(999L)).thenReturn(List.of());

        mockMvc.perform(get("/api/categories/parent/999"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void search_returnsMatchingCategories() throws Exception {
        Category cat1 = createCategory(1L, "uuid-1", "Electronics", "Electronics desc", "electronics", true, null);
        Category cat2 = createCategory(2L, "uuid-2", "Electronics Accessories", "Accessories desc", "electronics-accessories", true, null);
        when(categoryService.searchCategoryByName("electro")).thenReturn(List.of(cat1, cat2));

        mockMvc.perform(get("/api/categories/search")
                        .param("q", "electro"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Electronics"))
                .andExpect(jsonPath("$[1].name").value("Electronics Accessories"));
    }

    @Test
    void search_emptyResult_returnsEmptyArray() throws Exception {
        when(categoryService.searchCategoryByName("xyz")).thenReturn(List.of());

        mockMvc.perform(get("/api/categories/search")
                        .param("q", "xyz"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void create_validRequest_returnsCreatedCategory() throws Exception {
        Category created = createCategory(1L, "uuid-new", "New Category", "Description", "new-category", true, null);
        when(categoryService.createCategory(any(), any(), any(), any())).thenReturn(created);

        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test".getBytes());
        MockMultipartFile name = new MockMultipartFile("name", "", MediaType.TEXT_PLAIN_VALUE, "New Category".getBytes());
        MockMultipartFile description = new MockMultipartFile("description", "", MediaType.TEXT_PLAIN_VALUE, "Description".getBytes());

        mockMvc.perform(multipart("/api/categories")
                        .file(image)
                        .file(name)
                        .file(description))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Category"))
                .andExpect(jsonPath("$.slug").value("new-category"));
    }

    @Test
    void create_withoutImage_returnsCreatedCategory() throws Exception {
        Category created = createCategory(1L, "uuid-new", "New Category", "Description", "new-category", true, null);
        when(categoryService.createCategory(any(), any(), any(), any())).thenReturn(created);

        MockMultipartFile name = new MockMultipartFile("name", "", MediaType.TEXT_PLAIN_VALUE, "New Category".getBytes());
        MockMultipartFile description = new MockMultipartFile("description", "", MediaType.TEXT_PLAIN_VALUE, "Description".getBytes());

        mockMvc.perform(multipart("/api/categories")
                        .file(name)
                        .file(description))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Category"));
    }

    @Test
    void create_minimalRequest_returnsCreatedCategory() throws Exception {
        Category created = createCategory(1L, "uuid-new", "New Category", "Description", "new-category", true, null);
        when(categoryService.createCategory(any(), any(), any(), any())).thenReturn(created);

        MockMultipartFile name = new MockMultipartFile("name", "", MediaType.TEXT_PLAIN_VALUE, "New Category".getBytes());

        mockMvc.perform(multipart("/api/categories")
                        .file(name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Category"));
    }

    @Test
    void update_validRequest_returnsUpdatedCategory() throws Exception {
        Category updated = createCategory(1L, "uuid-1", "Updated Category", "Updated desc", "updated-category", false, null);
        when(categoryService.updateCategory(eq(1L), any(CategoryDto.class))).thenReturn(updated);

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Updated Category",
                                "description": "Updated desc",
                                "imageUrl": "updated.jpg",
                                "active": false,
                                "parent": 1
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Category"))
                .andExpect(jsonPath("$.slug").value("updated-category"))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void update_notFound_returnsNotFound() throws Exception {
        CategoryDto dto = new CategoryDto();
        dto.setName("Test");
        when(categoryService.updateCategory(eq(999L), any(CategoryDto.class)))
                .thenThrow(new ResourceNotFoundException("Category not found with id: 999"));

        mockMvc.perform(put("/api/categories/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Test"
                            }
                            """))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_invalidRequest_returnsBadRequest() throws Exception {
        when(categoryService.updateCategory(eq(1L), any(CategoryDto.class)))
                .thenThrow(new IllegalArgumentException("Category name length is must be 3 or more charctors"));

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": ""
                            }
                            """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateImage_validFile_returnsUpdatedCategory() throws Exception {
        Category updated = createCategory(1L, "uuid-1", "Electronics", "Electronics desc", "electronics", true, null);
        updated.setImageUrl("new-image.jpg");
        when(categoryService.updateCategoryImage(eq(1L), any())).thenReturn(updated);

        MockMultipartFile image = new MockMultipartFile("image", "new-image.jpg", MediaType.IMAGE_JPEG_VALUE, "test".getBytes());

        mockMvc.perform(multipart("/api/categories/1/image")
                        .file(image)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.imageUrl").value("new-image.jpg"));
    }

    @Test
    void updateImage_categoryNotFound_returnsInternalServerError() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test".getBytes());
        when(categoryService.updateCategoryImage(eq(999L), any()))
                .thenThrow(new RuntimeException("Category not found"));

        mockMvc.perform(multipart("/api/categories/999/image")
                        .file(image)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateImage_missingFile_returnsInternalServerError() throws Exception {
        mockMvc.perform(multipart("/api/categories/1/image")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void delete_exists_returnsNoContent() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_notFound_returnsBadRequest() throws Exception {
        doThrow(new IllegalStateException(""))
                .when(categoryService).deleteCategory(999L);

        mockMvc.perform(delete("/api/categories/999"))
                .andExpect(status().isBadRequest());
    }

    private Category createCategory(Long id, String uuid, String name, String description, String slug, Boolean active, Category parent) {
        Category category = new Category();
        category.setId(id);
        category.setUuid(uuid);
        category.setName(name);
        category.setDescription(description);
        category.setSlug(slug);
        category.setActive(active);
        category.setImageUrl("image-" + id + ".jpg");
        category.setParent(parent);
        return category;
    }
}