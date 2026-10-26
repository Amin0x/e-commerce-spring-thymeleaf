package com.alamin.ecommerce.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * Catalog REST controller for categories. Delegates to {@link CategoryService}
 * and returns JSON. The slug-based lookup was consolidated onto
 * {@code /api/categories/slug/{slug}} alongside the pre-existing
 * {@code /api/categories/{id}}.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> list() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/top")
    public ResponseEntity<List<Category>> top() {
        return ResponseEntity.ok(categoryService.getTopCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<Category> getBySlug(@PathVariable String slug) {
        return categoryService.findByCategorySlug(slug)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Category>> active(@RequestParam(defaultValue = "true") boolean active) {
        return ResponseEntity.ok(categoryService.getCategoriesByActive(active));
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<Category>> byParent(@PathVariable Long parentId) {
        return ResponseEntity.ok(categoryService.getSubcategoriesByParentId(parentId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Category>> search(@RequestParam String q) {
        return ResponseEntity.ok(categoryService.searchCategoryByName(q));
    }

    @PostMapping
    public ResponseEntity<Category> create(@RequestPart("name") String name,
                                           @RequestPart(required = false) String description,
                                           @RequestPart(required = false) Long parentId,
                                           @RequestPart(required = false) MultipartFile image) {
        Category created = categoryService.createCategory(name, description, parentId, image);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> update(@PathVariable Long id,
                                           @RequestBody CategoryDto categoryDto) {
        Category updated = categoryService.updateCategory(id, categoryDto);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Category> updateImage(@PathVariable Long id,
                                                @RequestParam MultipartFile image) {
        Category updated = categoryService.updateCategoryImage(id, image);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
