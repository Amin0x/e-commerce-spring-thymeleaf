package com.alamin.ecommerce.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getRandomCategories() {
        return categoryRepository.findRandomCategories();
    }

    // Get category by ID
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    // Delete category by ID
    public void deleteCategory(Long id) {
        Optional<Category> cat = getCategoryById(id);
        if(cat.isPresent()){
            Category curCat = cat.get();
            categoryRepository.updateCategoriesParent(curCat.getCategoryId(), curCat.getParent().getCategoryId());
            categoryRepository.deleteById(id);
            return;
        }

        throw new IllegalStateException("");
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Update category
    public Category updateCategory(Long id, CategoryDto categoryDto) {
        if (categoryDto == null) {
            throw new IllegalArgumentException("Updated category cannot be null");
        }

        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isPresent()) {
            Category existingCategory = categoryOptional.get();
            Category parent = categoryRepository.findById(Long.valueOf(categoryDto.getParent())).orElse(null);
            existingCategory.setParent(parent);
            existingCategory.setName(categoryDto.getName());
            existingCategory.setDescription(categoryDto.getDescription());
            existingCategory.setImageUrl(categoryDto.getImageUrl());
            existingCategory.setActive(categoryDto.getActive());
            existingCategory.setUpdated(LocalDateTime.now());
            return categoryRepository.save(existingCategory);
        }

        throw new IllegalArgumentException("Category not found with id: " + id);
    }

    // Get all categories with pagination and sorting
    public List<Category> getAllCategories(int page, int size, int order, boolean asc) {
        // Define the sorting column based on the "order" parameter
        String orderCol = null;
        switch (order) {
            case 1: orderCol = "name"; break;
            case 2: orderCol = "description"; break;
            default: orderCol = "name"; // Default to sorting by name
        }

        // Create the Sort object based on the orderCol and asc flag
        Sort sort = asc ? Sort.by(Sort.Order.asc(orderCol)) : Sort.by(Sort.Order.desc(orderCol));

        // Create a PageRequest with pagination and sorting
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        // Fetch the paged and sorted categories from the repository
        Page<Category> categoryPage = categoryRepository.findAll(pageRequest);

        // Return the list of categories (you can return the Page if you need pagination info)
        return categoryPage.getContent();
    }

    public boolean existsById(long id) {
        return categoryRepository.existsById(id);
    }

    public List<Category> searchCategoryByName(String search) {
        if (search == null)
            throw new IllegalArgumentException("null not allowed");
        return categoryRepository.searchCategoryByName(search);
    }
}
