package com.alamin.ecommerce.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // Create a new product
    @PostMapping("/api/products")
    @ResponseBody 
    public Product createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @GetMapping("/web/products/new")
    public String showCreateProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "create_product";
    }

    @PostMapping("/web/products")
    public String createProduct(@Valid Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "create_product";
        }

        productRepository.save(product);
        return "redirect:/web/products";
    }

    // Get all products
    @GetMapping("/api/products")
    @ResponseBody
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get all products
    @GetMapping("/web/products")
    public String getAllProducts(@RequestParam(required = false) String category, Model model) {
    
        List<Product> products;

        if (category == null || category.isEmpty()) {
            // Handle null or empty category
            products = productRepository.findAll();
    
        } else {
        
            try {
                products = productRepository.findByCategory(category);
            } catch (Exception e) {
                // Handle exceptions, e.g., category not found
                throw new CategoryNotFoundException("Category not found: " + category);
            }
        }
    
        model.addAttribute("products", products);
        return "products";

    }


    // Get a product by ID
    @GetMapping("/api/products/{id}")
    @ResponseBody
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update an existing product
    @PutMapping("/api/products/{id}")
    @ResponseBody
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        product.setId(id);
        Product updatedProduct = productRepository.save(product);
        return ResponseEntity.ok(updatedProduct);
    }

    // Delete a product by ID
    @DeleteMapping("/api/products/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
