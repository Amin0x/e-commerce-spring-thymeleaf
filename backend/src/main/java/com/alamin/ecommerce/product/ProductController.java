package com.alamin.ecommerce.product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * Catalog REST controller. Exposes {@link ProductService} as JSON for the
 * frontend (and any API client). Mirrors the existing Thymeleaf
 * {@code ProductController} behaviour without rendering views.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "created") String sortBy,
            @RequestParam(defaultValue = "true") boolean asc) {
        Sort sort = Sort.by(sortBy);
        if (!asc) {
            sort = sort.descending();
        }
        return ResponseEntity.ok(productService.getAllProducts(
                PageRequest.of(page, size, sort)).getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-uuid/{uuid}")
    public ResponseEntity<ProductDto> getByUuid(@PathVariable String uuid) {
        return productService.getProductByUuid(uuid)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-slug/{slug}")
    public ResponseEntity<ProductDto> getBySlug(@PathVariable String slug) {
        return productService.getProductBySlug(slug)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductDto> create(@Valid @RequestBody ProductRequest request) {
        ProductDto created = productService.save(request);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable Long id,
                                             @Valid @RequestBody ProductRequest updatedProduct) {
        return ResponseEntity.ok(productService.updateProduct(id, updatedProduct));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/random")
    public ResponseEntity<List<ProductDto>> random(@RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(productService.getRandomProducts(size));
    }

    @GetMapping("/best-selling")
    public ResponseEntity<List<ProductDto>> bestSelling(@RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(productService.getBestSellingProducts(size));
    }

    @GetMapping("/new-arrivals")
    public ResponseEntity<List<ProductDto>> newArrivals(@RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(productService.getLastAddedProducts(size));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDto>> byCategory(@PathVariable Long categoryId,
                                                       @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(productService.getRandomProductsByCategory(categoryId, size));
    }

    @GetMapping("/{productId}/reviews")
    public ResponseEntity<List<ProductReview>> reviews(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductReviews(productId));
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<?> uploadImage(@PathVariable Long id,
                                         @RequestParam MultipartFile file) {
        productService.uploadImage(file, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{productId}/reviews")
    public ResponseEntity<ProductReview> addReview(@PathVariable Long productId,
                                                   @RequestBody ProductReview review) {
        review.setProductId(productId);
        return ResponseEntity.ok(productService.createProductReview(review));
    }

}
