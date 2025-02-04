package com.alamin.ecommerce.product;

import com.alamin.ecommerce.category.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductImageRepository productImageRepository;

    public List<Product> getRandomProductsByCategory(String category) {
        return productRepository.findRandomProductsByCategory(category);
    }

    public List<Product> getNewArrivalProducts() {
        return productRepository.findNewArrivalProducts();
    }

    public Page<Product> getAllProducts(int pageNumber, int count) {
        if (pageNumber < 1)
            pageNumber = 1;

        if (count < 10)
            count = 10;

        Pageable pageable = PageRequest.of(pageNumber - 1, count);
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
        if (id == null){
            throw new RuntimeException("Product not found");
        }

        Optional<Product> existingProductOpt = productRepository.findById(id);
        if (existingProductOpt.isPresent()) {
            productRepository.deleteById(id);  // Delete product if it exists
        } else {
            throw new IllegalArgumentException("Product with ID " + id + " not found");
        }
    }

    public List<Category> getAllCategories() {
        return null;
    }

    public Long uploadImage(MultipartFile file) {
        String fn = file.getOriginalFilename();
        // Save the image file to the server
        try {
            String uploadDir = "path/to/upload/directory/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
               Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fn);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Return the ID of the image
            ProductImage pimg = productImageRepository.save(new ProductImage(fn, null));  // Save the image to the database
            return pimg.getId();
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + fn, e);
        }


    }

    public Product save(Product product) {
        if (product == null){
            throw new RuntimeException("Product not found");
        }
        return productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findByCategory(String category) {
        if (category == null){
            throw new RuntimeException("Product not found");
        }
        return productRepository.findByCategory(category);
    }

    public Optional<Product> findById(Long id) {
        if (id == null){
            throw new RuntimeException("Product not found");
        }
        return productRepository.findById(id);
    }

    public boolean existsById(Long id) {
        if (id == null){
            throw new RuntimeException("Product not found");
        }
        return productRepository.existsById(id);
    }

    public void deleteById(Long id) {
        if (id == null){
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }

    public List<Product> getBestSellingProducts() {
        return productRepository.getBestSellingProducts();
    }
}
