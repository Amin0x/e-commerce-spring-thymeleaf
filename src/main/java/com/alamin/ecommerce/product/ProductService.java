package com.alamin.ecommerce.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getRandomProductsByCategory(String category) {
        return productRepository.findRandomProductsByCategory(category);
    }

    public List<Product> getNewArrivalProducts() {
        return productRepository.findNewArrivalProducts();
    }

    public Page<Product> getAllProducts(int pageNumber, int count) {
        if (pageNumber < 1) pageNumber = 1;
        if (count < 10) count = 10;
        Pageable pageable = PageRequest.of(pageNumber - 1, count);  // 20 products per page
        return productRepository.findAll(pageable);
    }

    // Get product by ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);  // Returns an Optional, which may be empty if not found
    }

    // Update product
    public Product updateProduct(Long id, Product updatedProduct) {
        Optional<Product> existingProductOpt = productRepository.findById(id);
        if (existingProductOpt.isPresent()) {
            Product existingProduct = existingProductOpt.get();
            // Assuming the Product class has setters for the fields
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setCategory(updatedProduct.getCategory());
            existingProduct.setPrice(updatedProduct.getPrice());
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setStock(updatedProduct.getStock());
            // Update other fields as necessary

            return productRepository.save(existingProduct);  // Save the updated product
        } else {
            throw new IllegalArgumentException("Product with ID " + id + " not found");
        }
    }

    // Delete product by ID
    public void deleteProduct(Long id) {
        Optional<Product> existingProductOpt = productRepository.findById(id);
        if (existingProductOpt.isPresent()) {
            productRepository.deleteById(id);  // Delete product if it exists
        } else {
            throw new IllegalArgumentException("Product with ID " + id + " not found");
        }
    }
}
