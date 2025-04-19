package com.alamin.ecommerce.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.alamin.ecommerce.config.FileUploadService;
import com.alamin.ecommerce.exception.ResourceNotFoundException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private FileUploadService fileUploadService;

    @Override
    public List<Category> getRandomCategories() {
        return categoryRepository.findRandomCategories();
    }

    @Override
    public Optional<Category> getCategoryById(Long id) {

        return categoryRepository.findById(id);
    }

    // Delete category by ID
    @Override
    public void deleteCategory(Long id) {
        Optional<Category> cat = getCategoryById(id);
        if (cat.isPresent()) {
            Category curCat = cat.get();
            if (curCat.getParent() != null) {
                curCat.getParent().removeChild(curCat);
                curCat.setParent(null);
                categoryRepository.save(curCat);
            }
            // categoryRepository.updateCategoriesParent(curCat.getCategoryId(),
            // curCat.getParent().getCategoryId());
            categoryRepository.deleteById(id);
            return;
        }

        throw new IllegalStateException("");
    }

    @Override
    public Category createCategory(String name, String description, Long parentId, MultipartFile image) {
        String slug = generteSlug(name);
        Category category = new Category(name, description, null);
        category.setSlug(slug);
        category.setActive(false);
        category.setEnabled(false);
        category.setCreated(LocalDateTime.now());
        category.setUpdated(LocalDateTime.now());
        category.setImageUrl(null);
        category.setParent(null);
        category.setUuid(UUID.randomUUID().toString().replace("-", ""));

        if (parentId != null) {
            Optional<Category> parentOptional = getCategoryById(parentId);
            if (parentOptional.isPresent()) {
                Category parent = parentOptional.get();
                category.setParent(parent);
                parent.addChild(category);
            }
        }

        if (image != null) {
            String fileName = fileUploadService.uploadFile(image);
            category.setImageUrl("/uploads/" + fileName);
        } else {
            category.setImageUrl("/public/imgs/default.png");
        }
        return categoryRepository.save(category);
    }

    // Update category
    @Override
    public Category updateCategory(Long id, CategoryDto categoryDto) {
        if (categoryDto == null) {
            throw new IllegalArgumentException("Updated category cannot be null");
        }
        
        if (StringUtils.hasText(categoryDto.getName()) || categoryDto.getName().length() < 3) {
            throw new IllegalArgumentException("Category name length is must be 3 or more charctors");
        }

        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            Category parent = categoryRepository.findById(Long.valueOf(categoryDto.getParent())).orElse(null);
            category.setParent(parent);
            category.setName(categoryDto.getName());
            category.setSlug(generteSlug(categoryDto.getName()));
            //existingCategory.setEnabled(false);
            category.setActive(categoryDto.getActive());
            category.setDescription(categoryDto.getDescription());
            category.setUpdated(LocalDateTime.now());
            return categoryRepository.save(category);
        }

        throw new ResourceNotFoundException("Category not found with id: " + id);
    }

    // Get all categories with pagination and sorting
    @Override
    public List<Category> getAllCategories(int page, int size, int order, boolean asc) {
        // Define the sorting column based on the "order" parameter
        String orderCol = null;
        switch (order) {
            case 1:
                orderCol = "name";
                break;
            case 2:
                orderCol = "description";
                break;
            default:
                orderCol = "name"; // Default to sorting by name
        }

        // Create the Sort object based on the orderCol and asc flag
        Sort sort = asc ? Sort.by(Sort.Order.asc(orderCol)) : Sort.by(Sort.Order.desc(orderCol));

        // Create a PageRequest with pagination and sorting
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        // Fetch the paged and sorted categories from the repository
        Page<Category> categoryPage = categoryRepository.findAll(pageRequest);

        // Return the list of categories (you can return the Page if you need pagination
        // info)
        return categoryPage.getContent();
    }

    @Override
    public List<Category> getTopCategories() {
        return categoryRepository.getTopCategories();
    }

    @Override
    public boolean existsById(long id) {
        return categoryRepository.existsById(id);
    }

    @Override
    public List<Category> searchCategoryByName(String search) {
        if (search == null)
            throw new IllegalArgumentException("null not allowed");
        return categoryRepository.searchCategoryByName(search);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> getCategoriesByParentIdAndActive(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("null not allowed");
        }

        return categoryRepository.getCategoriesByParentIdAndActive(id);
    }

    @Override
    public List<Category> getCategoriesByParentIdAndActive(Long id, boolean active) {

        if (id == null)
            throw new IllegalArgumentException("null not allowed");

        return categoryRepository.getCategoriesByParentIdAndActive(id, active);
    }

    @Override
    public List<Category> getCategoriesByActive(boolean active) {

        return categoryRepository.getCategoriesByActive(active);
    }

    @Override
    public List<Category> getSubcategoriesByParentId(Long id) {

        if (id == null)
            throw new IllegalArgumentException("null not allowed");

        return categoryRepository.getSubcategoriesByParentId(id);
    }

    @Override
    public List<Category> getCategoryPathToRoot(Long categoryId) {

        if (categoryId == null)
            throw new IllegalArgumentException("null not allowed");

        return categoryRepository.getCategoryPathToRoot(categoryId);
    }

    @Override
    public Optional<Category> findByCategorySlug(String id) {
        if (id == null) {
            throw new IllegalArgumentException("null not allowed");
        }
        return categoryRepository.findBySlug(id);
    }

    @Override
    public Category updateCategoryImage(Long id, MultipartFile image) {
        String fileName = fileUploadService.uploadFile(image);
        Category category = getCategoryById(id).orElseThrow(() -> new ResourceNotFoundException("cant find category with id:" + id));
        category.setImageUrl(fileName);
        
        return categoryRepository.save(category);
    }

    private String generteSlug(String name){
        
        String slug = name.replaceAll("[^\\p{L}\\p{N}\\s]", "") // Allow Unicode letters and numbers
                          .replaceAll("\\s+", "-") // Replace spaces with hyphens
                          .replaceAll(" ", "-") // Replace spaces with hyphens
                          .replaceAll("[-]+", "-") // Replace multiple hyphens with a single hyphen
                            .replaceAll("[-]+$", "") // Remove trailing hyphens
                            .replaceAll("[-]+^", "") // Remove leading hyphens
                            .replaceAll("[_]+$", "") // Remove trailing underscores
                            .replaceAll("[_]+^", "") // Remove leading underscores
                          .toLowerCase();
        // Ensure the slug is unique by appending a random number if necessary
        // You can implement a check against your database to ensure uniqueness
        // For simplicity, this example just appends a random number
        // In a real application, you would check against existing slugs in the database
        //String randomNumber = String.valueOf((int) (Math.random() * 10000)); // Random number between 0 and 9999
        return slug;
    }
}
