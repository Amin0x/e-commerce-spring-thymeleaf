package com.alamin.ecommerce.category;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<Category> getRandomCategories();

    Optional<Category> getCategoryById(Long id);

    // Delete category by ID
    void deleteCategory(Long id);

    Category createCategory(String name, String description, Long parentId, MultipartFile image);

    // Update category
    Category updateCategory(Long id, CategoryDto categoryDto);

    Category updateCategoryImage(Long id, MultipartFile image);

    // Get all categories with pagination and sorting
    List<Category> getAllCategories(int page, int size, int order, boolean asc);

    boolean existsById(long id);

    List<Category> searchCategoryByName(String search);

    List<Category> findAll();

    List<Category> getCategoriesByParentIdAndActive(Long id);

    List<Category> getCategoriesByParentIdAndActive(Long id, boolean active);

    List<Category> getCategoriesByActive(boolean active);

    List<Category> getSubcategoriesByParentId(Long id);

    List<Category> getCategoryPathToRoot(Long categoryId);

    Optional<Category> findByCategorySlug(String id);

    List<Category> getTopCategories();
}
