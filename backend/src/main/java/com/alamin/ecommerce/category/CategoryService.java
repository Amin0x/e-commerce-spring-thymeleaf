package com.alamin.ecommerce.category;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    // Get random categories
    // This method is used to fetch a random set of categories from the database.
    List<Category> getRandomCategories();

    // Get category by ID
    Optional<Category> getCategoryById(Long id);

    // Delete category by ID
    void deleteCategory(Long id);

    // Create category
    Category createCategory(String name, String description, Long parentId, MultipartFile image);

    // Update category
    Category updateCategory(Long id, CategoryDto categoryDto);

    // Update category image
    Category updateCategoryImage(Long id, MultipartFile image);

    // Get all categories with pagination and sorting
    List<Category> getAllCategories(int page, int size, int order, boolean asc);

    boolean existsById(long id);

    // Get all categories with pagination and sorting
    List<Category> searchCategoryByName(String search);

    // Get all categories with pagination and sorting
    List<Category> findAll();

    List<Category> getCategoriesByParentIdAndActive(Long id);

    List<Category> getCategoriesByParentIdAndActive(Long id, boolean active);

    List<Category> getCategoriesByActive(boolean active);

    List<Category> getSubcategoriesByParentId(Long id);

    List<Category> getCategoryPathToRoot(Long categoryId);

    Optional<Category> findByCategorySlug(String id);

    List<Category> getTopCategories();
}
